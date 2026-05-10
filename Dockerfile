# Multi-stage build for FocusSphere Spring Boot application
# Stage 1: Builder
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Install Maven and other build dependencies
RUN apk add --no-cache maven

# Copy pom.xml
COPY pom.xml .

# Download dependencies (this caches them in the layer)
RUN mvn dependency:resolve

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 appuser && adduser -D -u 1001 -G appuser appuser

# Copy WAR from builder
COPY --from=builder /app/target/focussphere-0.0.1-SNAPSHOT.war app.war

# Change ownership
RUN chown -R appuser:appuser /app

USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/h2-console || exit 1

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.war"]
