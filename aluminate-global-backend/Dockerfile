# STAGE 1: Build stage with Maven + Java 17
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: Runtime stage with Java 17
FROM eclipse-temurin:17-jdk AS runtime
WORKDIR /app

RUN apt-get update && \
    apt-get install -y docker.io bash curl && \
    curl -SL https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

COPY --from=builder /app/target/*.jar app.jar

VOLUME /app/org-template

ARG GLOBAL_PORT
ENV SERVER_PORT=$GLOBAL_PORT
EXPOSE ${GLOBAL_PORT}

CMD ["java", "-jar", "app.jar"]
