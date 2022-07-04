Ne jamais placer d'identifiants / mdp sur des bases de données dans les fichiers suivants (module core, web, batch inclus)
- application.properties
- application-dev.properties
- application-test.properties
- application-prod.properties

Les valeurs manquantes sont remplies automatiquement au build / déploiement par jenkins
Elles sont consultables à l'adresse suivante :
https://jenkins.abes.fr/job/Qualimarc_back_multibranch_pipeline/credentials/
-> rubrique identifiants sur le panneau lateral de gauche, en bas, après avoir cliqué sur un job.

Les fichiers suivants :
web/src/main/resources/application-localhost.properties
core/src/test/resources/application.properties
contiennent les informations sensibles.

En cas de pull du projet, pour executer le projet localement, ces fichiers peuvent être récupérés à l'adresse suivante (gitlab de l'abes) :
