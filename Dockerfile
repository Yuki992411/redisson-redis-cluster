# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy module build files
COPY application/build.gradle.kts application/
COPY domain/build.gradle.kts domain/
COPY infrastructure/build.gradle.kts infrastructure/

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY application/src application/src
COPY domain/src domain/src
COPY infrastructure/src infrastructure/src

# Build application
RUN ./gradlew :application:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Copy jar from builder stage
COPY --from=builder /app/application/build/libs/*.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]