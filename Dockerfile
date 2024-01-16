FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} neupinion.jar

EXPOSE 8080 8090
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/neupinion.jar"]
