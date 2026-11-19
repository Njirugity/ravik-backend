FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S ravik && adduser -S ravik -G ravik
COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /app/uploads && chown -R ravik:ravik /app
USER ravik
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
