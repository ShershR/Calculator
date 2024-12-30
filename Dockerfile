# Этап сборки
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Этап выполнения
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/calculator-1.0.jar app.jar
CMD ["java", "-jar", "app.jar"]
