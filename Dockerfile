FROM maven:3.9.11-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jdk AS runner

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 4004

ENTRYPOINT ["java", "-jar", "app.jar"]