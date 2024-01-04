FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY gradlew .
COPY gradle gradle
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} neupinion.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/neupinion.jar"]
