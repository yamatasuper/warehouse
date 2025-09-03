# Use a multi-architecture compatible base image
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -x test --no-daemon

# Use the same base for runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY src/main/resources/application-docker.yml /app/config/application-docker.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/config/application-docker.yml"]