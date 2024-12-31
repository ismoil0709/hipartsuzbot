FROM maven:3.8.3-openjdk-17 AS build-main
WORKDIR /app
COPY . .
RUN mkdir -p lib
RUN docker pull ismoil0709/eskiz-plugin:latest
COPY --from=ismoil0709/eskiz-plugin:latest /app/eskiz-plugin-0.0.1.jar /app/eskiz-plugin-0.0.1.jar
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build-main /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]