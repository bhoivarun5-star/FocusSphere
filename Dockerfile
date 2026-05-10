# Multi-stage build for optimal image size and security
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests=true -q

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy WAR from builder stage
COPY --from=builder /build/target/focussphere-0.0.1-SNAPSHOT.war ./app.war

# Create non-root user for security
RUN addgroup -g 1000 focussphere && \
    adduser -D -u 1000 -G focussphere focussphere && \
    chown -R focussphere:focussphere /app

USER focussphere

# Expose port (will be overridden by PORT env var)
EXPOSE 8080

# Default to production profile, can be overridden via SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=render \
    PORT=8080 \
    JAVA_OPTS="-Xms512m -Xmx1024m"

# Health check - increased timeout for startup
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
    CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application with proper memory settings
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.war"]
