# this docker file is only suitable for dev, for production we need a separate one. this one probably enables hot reloading.
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean

# Set env variables (from Docker Compose)
ARG GLOBAL_PORT
ENV SERVER_PORT=$GLOBAL_PORT

# The pom.xml and src will be mounted as volumes
EXPOSE $GLOBAL_PORT

# Run with spring-boot:run for development (enables hot reload)
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]
