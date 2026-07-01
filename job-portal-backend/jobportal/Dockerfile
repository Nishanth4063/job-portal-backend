# Stage 1: Build the application using Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Pack the application into an executable JAR file without running unit tests
RUN mvn clean package -DskipTests

# Stage 2: Execute the package in a lightweight runtime environment
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR file from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose the Spring Boot embedded TomCat port
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]