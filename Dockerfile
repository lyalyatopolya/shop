# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Pproduction -DskipTests

# Этап запуска
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/shop.jar .
EXPOSE 8081
ENTRYPOINT ["java",
            "-Xms1024m",
            "-Xmx2048m",
            "-jar", "shop.jar"]

