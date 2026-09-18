# 1. Build Application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Run & Serve
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/bootstrap/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]