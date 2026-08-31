# Correctif « Timeout Oracle BaseXML et durcissement du moteur d'analyse asynchrone »

**Branche** : `fix/timeout-basexml-durcissement-async`
**Date** : 31/08/2026
**Déclencheur** : incident TEST du 28 au 31/08/2026 — analyses de plus en plus lentes, puis
`TaskRejectedException` et blocage total du service d'analyse (barre de progression à 0 %),
alors que la PROD restait fonctionnelle.

---

## 1. Résumé de l'incident

L'API lit les notices dans la base Oracle **BaseXML** avant chaque analyse. Aucun timeout
JDBC n'était configuré : quand l'Oracle de test a cessé de répondre proprement, les threads
d'analyse sont restés **bloqués indéfiniment** sur la lecture du CLOB XML.

Le moteur d'analyse asynchrone (livré en juillet 2026) ne dispose que de **8 threads** :
chaque thread bloqué étant perdu jusqu'au redémarrage, le pool s'est épuisé en quelques
jours, puis toute nouvelle analyse a été rejetée (`TaskRejectedException`) et même la
consultation du catalogue de règles a fini par pâtir de la saturation des connexions.

Le correctif traite la cause (les attentes infinies) et durcit le moteur asynchrone pour
que ce scénario se traduise désormais par des erreurs claires et récupérables, jamais par
une panne globale.

---

## 2. Correctif n°1 — Timeouts JDBC sur la datasource BaseXML (cause racine)

**Fichiers** :
- `core/src/main/java/fr/abes/qualimarc/core/configuration/BaseXmlConfig.java`
- `web/src/main/resources/application-{dev,test,prod}.properties`

### Avant

```java
// BaseXmlConfig.java
@Bean
@ConfigurationProperties(prefix = "spring.datasource.basexml")
public DataSource baseXmlDataSource() {
    return DataSourceBuilder.create().build();
}
```

Aucun timeout : ni à l'établissement de la connexion, ni à la lecture de la réponse.
Un Oracle muet = un thread bloqué **à vie**.

### Après

```java
@Bean
@ConfigurationProperties(prefix = "spring.datasource.basexml")
public DataSource baseXmlDataSource() {
    HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
    dataSource.addDataSourceProperty("oracle.net.CONNECT_TIMEOUT", basexmlConnectTimeout);
    dataSource.addDataSourceProperty("oracle.jdbc.ReadTimeout", basexmlReadTimeout);
    return dataSource;
}
```

| Propriété (nouvelle) | Valeur | Effet |
|---|---|---|
| `qualimarc.basexml.connect-timeout` | 10 000 ms | Échec si la connexion Oracle ne s'établit pas en 10 s |
| `qualimarc.basexml.read-timeout` | 60 000 ms | Échec si la lecture d'une réponse (y compris le CLOB de la notice) dépasse 60 s |
| `spring.datasource.basexml.connection-timeout` | 10 000 ms | Hikari : échec rapide si aucune connexion disponible dans le pool (au lieu de 30 s) |
| `spring.datasource.basexml.leak-detection-threshold` | 120 000 ms | Hikari logue toute connexion gardée > 2 min → alerte précoce d'un thread bloqué |

**Comportement résultant** : si BaseXML ne répond plus, la lecture échoue proprement,
le thread est libéré, la connexion rendue au pool, et l'analyse remonte une erreur sur le
PPN concerné au lieu de figer tout le service.

---

## 3. Correctif n°2 — Durcissement du moteur d'analyse asynchrone

**Fichiers** :
- `web/src/main/java/fr/abes/qualimarc/web/controller/RuleController.java`
- `web/src/main/java/fr/abes/qualimarc/QualimarcAPIApplication.java`

### 3.1 Rejet propre quand le pool sature (HTTP 503 au lieu de 500)

**Avant** : quand le pool (8 threads) et sa file d'attente (100) étaient saturés,
`CompletableFuture.supplyAsync` levait une `TaskRejectedException` qui remontait en
**HTTP 500 opaque** — illisible pour l'utilisateur et les équipes.

**Après** : la `TaskRejectedException` est attrapée dans `checkPpn` ; l'API renvoie
**HTTP 503** (service temporairement indisponible) avec un log WARN explicite, et nettoie
l'état de l'analyse refusée. Le front peut afficher un message clair et l'utilisateur
peut réessayer.

### 3.2 Garde-fou : nombre maximal d'analyses simultanées (nouveau)

**Avant** : rien ne limitait le nombre d'analyses concurrentes. Or chaque analyse
mobilise plusieurs threads du pool ; dès ~3 analyses simultanées, elles s'affamaient
entre elles et dégradaient tout le service.

**Après** : au-delà de `qualimarc.analysis.max-concurrent` analyses en cours
(2 par défaut), la nouvelle demande est refusée immédiatement en **HTTP 503**
avant même de toucher au pool. Le service reste opérationnel pour les analyses en cours.

### 3.3 Timeout global sur chaque analyse (nouveau)

**Avant** : une analyse coincée restait pendante **indéfiniment** ; l'utilisateur voyait
sa barre de progression figée à zéro sans aucun retour.

**Après** : `analysisFuture.orTimeout(qualimarc.analysis.timeout-minutes, MINUTES)`
(30 min par défaut) — au-delà, l'analyse est déclarée en échec et le client reçoit une
erreur explicite via `/result/{id}`.

### 3.4 Fusion des résultats sans auto-soumission au pool

**Avant** : la fusion des partitions utilisait
`thenCombineAsync(..., asyncExecutor)` — des tâches supplémentaires soumises **au même
pool** que les analyses elles-mêmes, en concurrence avec elles (risque d'affamation dès
plusieurs analyses parallèles).

**Après** : attente de toutes les partitions (`CompletableFuture.allOf(...).join()`)
puis fusion **synchrone** des résultats. Aucune tâche supplémentaire n'est soumise au
pool ; le thread « orchestrateur » est le seul mobilisé par l'analyse elle-même.

### 3.5 Purge périodique des analyses orphelines (nouveau)

**Avant** : une entrée d'`analysisResultsById` n'était retirée que si le client revenait
chercher son résultat. Utilisateur qui ferme son onglet = future **en mémoire pour
toujours** (fuite mémoire lente).

**Après** : `@Scheduled(PT5M) purgeFinishedAnalyses()` (scheduling activé via
`@EnableScheduling` sur `QualimarcAPIApplication`) retire toutes les 5 minutes les
analyses terminées et celles qui ont dépassé deux fois le délai d'analyse.

---

## 4. Tableau de synthèse avant / après

| Scénario | Avant | Après |
|---|---|---|
| Oracle BaseXML muet | Threads bloqués à vie, épuisement du pool, panne globale | Échec en 60 s max, thread libéré, erreur propre sur le PPN |
| Pool asynchrone saturé | HTTP 500 opaque (`TaskRejectedException`) | HTTP 503 clair + log WARN |
| ≥ 3 analyses simultanées | Affamation du pool, dégradation générale | Les suivantes refusées en 503, les autres terminent normalement |
| Analyse coincée | Barre de progression figée à 0 % pour toujours | Échec déclaré au bout de 30 min, erreur visible |
| Onglet fermé par l'utilisateur | Fuite mémoire (futures jamais nettoyés) | Purge automatique toutes les 5 min |
| Fusion multi-partitions | Tâches de fusion en concurrence avec les analyses | Fusion synchrone, zéro tâche supplémentaire |
| Connexion pool Hikari | Attente 30 s par défaut | Échec en 10 s + détection de fuite loguée (2 min) |

---

## 5. Paramètres ajoutés (récapitulatif)

Tous les paramètres ont une valeur par défaut sûre dans le code ; les profils
`dev`/`test`/`prod` les explicitent :

| Clé | Valeur appliquée | Rôle |
|---|---|---|
| `qualimarc.basexml.connect-timeout` | 10000 | Timeout connexion Oracle (ms) |
| `qualimarc.basexml.read-timeout` | 60000 | Timeout lecture Oracle (ms) |
| `qualimarc.analysis.max-concurrent` | 2 | Nombre max d'analyses simultanées |
| `qualimarc.analysis.timeout-minutes` | 30 | Durée max d'une analyse (min) |

---

## 6. Vérifications effectuées

- `mvn test -pl core,web -am` : **BUILD SUCCESS** — 214 tests core + 67 tests web,
  dont les 11 tests `RuleControllerTest` (analyse mono-thread, multi-thread, polling
  `/result` pendant l'exécution, indexation des règles) tous verts.

## 7. Hors périmètre (tickets de suivi)

- Séparation des pools « orchestrateur » et « travailleurs » du moteur asynchrone
  (refactor plus large).
- Signalement de l'instabilité de l'Oracle APISUDOC de TEST à l'équipe BaseXML.
- Correctifs null-safety identifiés après la MEP du 28/08 (unboxing `Boolean` dans
  `ComplexRule`, validation `@NotNull` du DTO d'analyse).
