# Multi-stage build for FocusSphere
# Stage 1: Build stage with Maven
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime stage with minimal JRE
FROM eclipse-temurin:17-jre-alpine

# Create non-root user for security
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/target/focussphere-*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/h2-console || exit 1

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=local
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Run the application
CMD ["java", "-jar", "app.jar"]
