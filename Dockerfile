FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 2580

ENTRYPOINT ["java", "-jar", "app.jar"]