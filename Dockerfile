###
# Image pour la compilation
FROM maven:3-jdk-11 as build-image
WORKDIR /build/
# Installation et configuration de la locale FR
RUN apt update && DEBIAN_FRONTEND=noninteractive apt -y install locales
RUN sed -i '/fr_FR.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG fr_FR.UTF-8
ENV LANGUAGE fr_FR:fr
ENV LC_ALL fr_FR.UTF-8
# On lance la compilation
# si on a un .m2 local on peut décommenter la ligne suivante pour 
# éviter à maven de retélécharger toutes les dépendances
#COPY ./.m2/    /root/.m2/
COPY ./pom.xml /build/pom.xml
COPY ./core/   /build/core/
COPY ./web/    /build/web/
RUN mvn --batch-mode \
        -Dmaven.test.skip=false \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package


###
# Image pour le module web
# FROM tomcat:9-jdk11 as web-image
# COPY --from=build-image /build/web/target/*.war /usr/local/tomcat/webapps/ROOT.war
# CMD [ "catalina.sh", "run" ]
FROM openjdk:11 as web-image
WORKDIR /app/
COPY --from=build-image /build/web/target/*.jar /app/kalidoc.jar
ENTRYPOINT ["java","-jar","/app/kalidoc.jar"]
