ARG MODULE_NAME=auth_service

# ==================== Шаг 1: Сборка артефакта ====================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# 1. Копируем корневой pom.xml проекта
COPY pom.xml .

# 2. КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: Копируем POM-файлы АБСОЛЮТНО ВСЕХ модулей,
# чтобы Maven мог успешно просканировать структуру многомодульного проекта
COPY shared-api/pom.xml ./shared-api/
COPY auth_service/pom.xml ./auth_service/
COPY review_service/pom.xml ./review_service/
COPY analysis_service/pom.xml ./analysis_service/

# 3. Скачиваем все зависимости для сборки (этот слой закэшируется, если pom-файлы не менялись)
RUN mvn dependency:go-offline -B

# 4. Копируем реальный исходный код общих библиотек и текущего компилируемого сервиса
COPY shared-api/ ./shared-api/
ARG MODULE_NAME
COPY ${MODULE_NAME}/ ./${MODULE_NAME}/

# 5. Собираем исполняемый Fat-JAR файл конкретного микросервиса
RUN mvn -pl ${MODULE_NAME} clean package -DskipTests -B

# ==================== Шаг 2: Легковесный рантайм-образ ====================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Создаем непривилегированного пользователя для безопасности рантайма
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем JAR-файл из предыдущего шага сборщика динамически по имени модуля
ARG MODULE_NAME
COPY --from=builder /app/${MODULE_NAME}/target/*.jar app.jar

# Порт сервиса (для auth — 8081, для auth — 8082, для analysis — 8083)
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]