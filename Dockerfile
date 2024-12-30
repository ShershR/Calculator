FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

# Используем официальный образ Java с нужной версией JDK
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем скомпилированный JAR-файл в контейнер
COPY target/calculator-1.0.jar app.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "app.jar"]