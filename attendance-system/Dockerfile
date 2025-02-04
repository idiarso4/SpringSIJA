# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app

# Install system dependencies for OpenCV
RUN apt-get update && apt-get install -y \
    libopencv-dev \
    && rm -rf /var/lib/apt/lists/*

# Copy built application
COPY --from=build /app/target/*.jar app.jar

# Copy application configuration
COPY src/main/resources/application.yml /app/config/

# Create directory for file uploads
RUN mkdir -p /app/uploads

# Environment variables
ENV SPRING_CONFIG_LOCATION=file:/app/config/application.yml
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Expose port
EXPOSE 8080

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
