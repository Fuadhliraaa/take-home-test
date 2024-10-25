# Start with OpenJDK 21 base image
FROM openjdk:21-jdk-slim AS build

# Set the Gradle version you want to install
ENV GRADLE_VERSION=8.3

# Install necessary tools and download Gradle
RUN apt-get update && apt-get install -y wget unzip \
    && wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /tmp \
    && unzip /tmp/gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle \
    && rm /tmp/gradle-${GRADLE_VERSION}-bin.zip

# Add Gradle to PATH
ENV PATH="/opt/gradle/bin:${PATH}"

# Set the working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the project
RUN gradle clean build -x test -x check

# Use a smaller runtime image to run the app
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]