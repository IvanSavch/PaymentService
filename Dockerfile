FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM alpine/java:21-jre
COPY --from=build /app/target/*.jar PaymentServiceApp.jar
ENTRYPOINT ["java","-jar","PaymentServiceApp.jar"]