# STAGE 1: Build stage with Maven
FROM maven:3.9 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: Runtime stage with OpenJDK 21
FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app


# Install docker + docker compose plugin
RUN apt-get update && \
    apt-get install -y docker.io bash curl && \
    curl -SL https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose


# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Mount org-template directory inside container (for docker-compose)
VOLUME /app/org-template

# Set environment variables
ARG GLOBAL_PORT
ENV SERVER_PORT=$GLOBAL_PORT
EXPOSE $GLOBAL_PORT

# Run the application
CMD ["java", "-jar", "app.jar"]
