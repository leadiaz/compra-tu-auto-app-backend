# Dockerfile para Spring Boot + PostgreSQL
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/pdss22025-0.0.1-SNAPSHOT.jar"]
