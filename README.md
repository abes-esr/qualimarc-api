# qualimarc-api

[![build-test-pubtodockerhub](https://github.com/abes-esr/qualimarc-api/actions/workflows/build-test-pubtodockerhub.yml/badge.svg)](https://github.com/abes-esr/qualimarc-api/actions/workflows/build-test-pubtodockerhub.yml) [![Docker Pulls](https://img.shields.io/docker/pulls/abesesr/qualimarc.svg)](https://hub.docker.com/r/abesesr/qualimarc/)

Qualimarc est l'outil de diagnostic des notices du Sudoc.

![qualimarc](https://user-images.githubusercontent.com/328244/203315079-4cabb49a-58a8-4778-80b5-d789e48fb94d.PNG)

Ce dépôt héberge le code source de l'API de qualimarc.  
Cette API fonctionne avec son interface utilisateur développée en VueJS (front) : https://github.com/abes-esr/qualimarc-front/  
Et l'application qualimarc complète peut être déployée via Docker à l'aide de ce dépôt : https://github.com/abes-esr/qualimarc-docker/  

Règles de qualimarc: https://github.com/abes-esr/qualimarc-rules/

(swagger : http://monUrlqualimarc:0000/swagger-ui/index.html)


## Développements

### Générer l'image docker en local

Vous pouvez avoir besoin de générer en local l'image docker de ``qualimarc-api`` par exemple si vous cherchez à modifier des liens entre les conteneurs docker de qualimarc ou bien si vous devez modifier l'architecture technique de qualimarc (ex: ajouter une base de données postgresql à qualimarc).

Pour générer l'image docker de ``qualimarc-api`` en local voici la commande à lancer :
```bash
cd qualimarc-api/
docker build -t abesesr/qualimarc:develop-api .
```

Cette commande aura pour effet de générer une image docker sur votre poste en local avec le tag ``develop-api``. Vous pouvez alors déployer qualimarc en local avec docker en vous utilisant sur le [dépot ``qualimarc-docker``](https://github.com/abes-esr/qualimarc-docker) et en prenant soins de régler la variable ``QUALIMARC_API_VERSION`` sur la valeur ``develop-api`` (c'est sa [valeur par défaut](https://github.com/abes-esr/qualimarc-docker/blob/e849157904619778d461c584a5bb770edb1fa667/.env-dist#L20)) dans le fichier ``.env`` de votre déploiement [``qualimarc-docker``](https://github.com/abes-esr/qualimarc-docker).

Vous pouvez utiliser la même procédure pour générer en local l'image docker de ``qualimarc-front``, la seule chose qui changera sera le nom du tag docker qui sera ``develop-front`` et qu'il faudra positionner dans la variable ``QUALIMARC_FRONT_VERSION`` de votre fichier ``.env``.


Cette commande suppose que vous disposez d'un environnement Docker en local : cf la [FAQ dans la poldev](https://github.com/abes-esr/abes-politique-developpement/blob/main/10-FAQ.md#configuration-dun-environnement-docker-sous-windows-10).
