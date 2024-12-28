FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/user-service-0.0.1-SNAPSHOT.jar user-service.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]