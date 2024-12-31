FROM maven:3.8.3-openjdk-17 AS build-main
WORKDIR /app
COPY . .
COPY lib/eskiz-plugin-0.0.1.jar lib/eskiz-plugin-0.0.1.jar
RUN mvn install:install-file -Dfile=lib/eskiz-plugin-0.0.1.jar \
          -DgroupId=uz.eskiz \
          -DartifactId=eskiz-plugin \
          -Dversion=0.0.1 \
          -Dpackaging=jar

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build-main /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]