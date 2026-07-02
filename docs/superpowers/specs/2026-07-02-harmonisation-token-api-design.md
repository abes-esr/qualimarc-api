# Design - Harmonisation du token API

## Contexte

L'API Qualimarc protege certains endpoints d'administration avec un JWT partage entre environnements. Le code actuel genere aussi un token au demarrage de l'application et l'affiche sur la sortie standard. Ce comportement cree un risque de fuite du secret d'exploitation et rend le fonctionnement dependant d'un token calcule localement plutot que du token configure.

En parallele, le job Ansible de production attend un token precis. L'objectif immediat est donc de corriger le point de securite critique sans casser l'exploitation existante.

## Objectif

Corriger en priorite le point 1 du rapport en faisant reposer l'authentification uniquement sur un token configure, non genere au boot, et harmoniser la valeur attendue entre `dev`, `test` et `prod`.

## Perimetre

- `web/src/main/java/fr/abes/qualimarc/QualimarcAPIApplication.java`
- `web/src/main/java/fr/abes/qualimarc/web/security/*`
- `web/src/main/resources/application-dev.properties`
- `web/src/main/resources/application-test.properties`
- `web/src/main/resources/application-prod.properties`
- tests `web/src/test`

Hors perimetre pour cette etape:

- refonte complete du mecanisme d'authentification admin
- rotation de secret ou externalisation vers un coffre-fort
- correction des autres points du rapport

## Approche retenue

### 1. Supprimer la generation du token au demarrage

L'application ne doit plus generer de JWT au boot et ne doit plus l'afficher sur la sortie standard. Le contexte de securite initialise localement au demarrage sera retire.

### 2. Conserver la validation JWT cote filtre

Le filtre HTTP continue de valider les JWT recus via le header `Authorization: Bearer ...`, mais uniquement a partir du secret configure par environnement.

### 3. Harmoniser la configuration des environnements

Les fichiers `application-dev.properties`, `application-test.properties` et `application-prod.properties` seront alignes sur la meme valeur de `jwt.anonymousUser` et de `jwt.secret`, en coherence avec le token de production transmis pour Ansible.

Important: la valeur du secret configure doit etre compatible avec le token deja utilise en production. Le but de cette etape est la compatibilite d'exploitation, pas l'amelioration finale du modele de secret.

## Impacts attendus

- plus aucun token ne sera imprime au demarrage
- les jobs et appels existants continueront a fonctionner s'ils utilisent le token partage attendu
- le comportement sera stable entre `dev`, `test` et `prod`

## Tests prevus

- test unitaire ou d'integration verifiant qu'aucune generation de token au demarrage n'est necessaire
- test sur `JwtTokenProvider` ou `JwtAuthenticationFilter` validant qu'un token connu reste accepte avec la configuration harmonisee
- execution de `mvn test` au minimum sur le module `web`, idealement sur l'ensemble du projet si le temps de cycle reste acceptable

## Risques et garde-fous

- risque: mauvais couple `jwt.anonymousUser` / `jwt.secret` et rupture des appels Ansible
  garde-fou: ajouter un test avec le token partage fourni
- risque: dependance implicite a l'initialisation du `SecurityContextHolder` au boot
  garde-fou: supprimer le code de boot puis verifier les tests web

## Resultat attendu

Une branche dediee porte un correctif minimal et exploitable qui ferme la fuite la plus critique du rapport tout en alignant `dev`, `test` et `prod` sur le meme token d'administration.
