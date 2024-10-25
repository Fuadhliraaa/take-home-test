# Use the official Gradle image with Java 21 for building
FROM gradle:7.6.0-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle clean build -x test -x check

# Use the official OpenJDK 21 runtime for running the app
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]