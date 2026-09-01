# Retour au moteur d'analyse synchrone (option retenue par l'équipe)

**Branche** : `revert/analyse-synchrone-avant-4dbbe9d`
**Date** : 31/08/2026 — **décision du 01/09/2026 : option synchrone retenue**
**Objet** : retour au moteur synchrone à la suite de l'incident TEST du 28 au 31/08/2026
(épuisement du pool asynchrone sur lectures Oracle BaseXML sans timeout).

> ✅ **Décision d'équipe du 01/09/2026 : retour au mode synchrone.**
> À merger en coordination avec la contrepartie front :
> [qualimarc-front PR #207](https://github.com/abes-esr/qualimarc-front/pull/207).
> L'option « asynchrone durci » (PR #235) est fermée ; les timeouts JDBC BaseXML
> (volet indépendant du choix de moteur) ont été reportés sur cette branche.

---

## Ce que fait cette PR

Retour de l'API à l'état **pré-`4dbbe9d`** (« Rend l'analyse de l'API asynchrone », 08/07/2026),
c'est-à-dire le moteur d'analyse **synchrone** :

- `/api/v1/check` redevient synchrone : il renvoie directement le résultat complet
  (plus de 202 + récupération différée via `/result/{id}` ; la barre de progression
  conserve le polling `/getStatus` qui existait avant l'asynchronisation) ;
- suppression du fan-out des PPN en partitions parallèles et de l'attente `.join()` ;
- suppression du DTO `AnalysisLaunchResponseDto` ;
- annulation de l'optimisation « chemin chaud » liée au suivi de progression (PR #230,
  `isolation-suivi-progression`), préparée pour l'asynchrone.

Fichiers restaurés à leur état d'avant l'asynchronisation :
`RuleController`, `RuleService`, `NoticeService`, `NoticeXml`, `ComplexRule` + tests associés.

## Ce qui est CONSERVÉ (non régressé)

- Toutes les fonctionnalités règles : SOA-179 (affichage-etiquette), SOA-181 (bornes de
  dépendance, position -1), SOA-182 (position de sous-zone, groupe même zone imbriqué),
  SOA-183 (positionStart/End, comparateurs) ;
- l'harmonisation du token API (PR #226) et le nettoyage des warnings Spring/Hibernate (PR #233) ;
- `AsyncConfiguration` (antérieure à l'asynchronisation, issue du POC multithread).

## Contrepartie front OBLIGATOIRE

Le front a été adapté à l'API asynchrone par la **PR #205** (`analyse-asynchrone-check-front`,
commits `cb6b622` « Stabilise le polling de progression » et `dc1e4cb` « Consomme le résultat
asynchrone dans le front »), mergée via **#206**. Si cette option est retenue, il faudra
également revenir en arrière côté `qualimarc-front`, sinon l'IHM sera cassée.

## Comparaison des options pour l'arbitrage

| Critère | Option A — garder l'asynchrone durci (PR #235) | Option B — retour au synchrone (cette PR) |
|---|---|---|
| Expérience utilisateur | Lancement immédiat, barre de progression, pas de coupure navigateur | Attente bloquante pendant toute l'analyse ; coupure si le navigateur/proxy timeout |
| Grosses listes de PPN | Découpées en parallèle (8 threads) | Traitées séquentiellement, plus long |
| Comportement si Oracle BaseXML tousse | Erreurs bornées (timeouts JDBC), service disponible | Chaque requête bloquée consomme 1 thread HTTP sur ~200 : dégradation lente, seuil plus haut |
| Surface de code | Moteur asynchrone (futures, purge, garde-fous) | Code plus simple, moins de cas limites |
| Charge de mise en œuvre | Déjà prête (PR #235), API seule | API + retour arrière front + requalification complète |

## Vérifications effectuées sur cette branche

`mvn test -pl core,web -am` : **BUILD SUCCESS** — 211 tests core + 66 tests web verts
(dont indexation des règles, dépendances à position négative, bornes minuscules, groupe
même zone imbriqué).

## Recommandation technique (à titre indicatif)

L'incident TEST n'a pas été causé par le choix asynchrone lui-même mais par l'absence de
timeouts JDBC ; l'option A traite cette cause tout en conservant l'UX. L'option B supprime
le pool de 8 threads mais réintroduit la dépendance aux timeouts navigateur/proxy et
rallonge les analyses volumineuses. La décision appartient à l'équipe.
