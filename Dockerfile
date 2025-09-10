FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# Use wildcard to find the actual JAR file name
COPY --from=build /app/build/libs/*.jar app.jar
COPY src/main/resources/application-docker.yml /app/config/application-docker.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/config/application-docker.yml", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]