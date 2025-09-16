# Multi-stage build for Spring Boot application
FROM maven:3.8.6-openjdk-11-slim AS build

# Set working directory
WORKDIR /app

# Copy pom files first (for better layer caching)
COPY pom.xml .
COPY weblog-web/pom.xml weblog-web/
COPY weblog-module-admin/pom.xml weblog-module-admin/
COPY weblog-module-common/pom.xml weblog-module-common/
COPY weblog-module-jwt/pom.xml weblog-module-jwt/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user
RUN addgroup --system app && adduser --system app --ingroup app

# Set working directory
WORKDIR /app

# Copy application jar from build stage
COPY --from=build /app/weblog-web/target/weblog-web-*.jar app.jar

# Copy docker-specific configuration
COPY --from=build /app/../docker/application-docker.yml application-docker.yml

# Create directories
RUN mkdir -p /app/logs /app/lucene-index

# Change ownership
RUN chown -R app:app /app

# Switch to app user
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]