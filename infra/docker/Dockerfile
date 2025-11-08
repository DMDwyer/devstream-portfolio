FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
CMD ["java","-jar","target/devstream-portfolio-0.1.1-SNAPSHOT.jar"]