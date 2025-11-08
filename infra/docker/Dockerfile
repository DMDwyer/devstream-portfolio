# --- Build stage ---
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/devstream-portfolio-0.0.1-SNAPSHOT.jar .
CMD ["java","-jar","devstream-portfolio-0.0.1-SNAPSHOT.jar"]