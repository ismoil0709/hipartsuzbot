FROM maven:3.8.3-openjdk-17 AS build-plugin
WORKDIR /plugin
COPY ../eskiz-plugin-java .
RUN mvn clean package -DskipTests

FROM maven:3.8.3-openjdk-17 AS build-main
WORKDIR /app
COPY . .
RUN mkdir -p lib
COPY --from=build-plugin /plugin/target/eskiz-plugin-0.0.1.jar /app/lib/eskiz-plugin-0.0.1.jar
RUN mvn clean package -DskipTests

# Stage 3: Create the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build-main /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
