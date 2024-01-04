FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} neupinion.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/neupinion.jar"]