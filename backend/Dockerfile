# --- Stage 1: Build the Application ---
# We use a Docker image that has Maven pre-installed
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy all your backend files into the builder
COPY . .

# Run the build command (skip tests to be faster)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
# Now we switch to a tiny, empty Java 21 image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy ONLY the finished JAR file from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]