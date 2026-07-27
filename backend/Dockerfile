# ==============================================================================
# Hyperlofy Backend - Production Grade Multi-Stage Dockerfile
# ==============================================================================

# STAGE 1: Build & Package
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy POM and download dependencies to optimize caching layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production JAR
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: Lightweight Production JRE Runtime
FROM eclipse-temurin:21-jre-alpine AS runner

# Install curl for container healthchecks
RUN apk add --no-cache curl tzdata

# Set Production Timezone
ENV TZ=Asia/Kolkata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Security Hardening: Create non-root user and group
RUN addgroup -S hyperlofy && adduser -S hyperlofy -G hyperlofy

WORKDIR /app

# Copy compiled JAR artifact from builder stage
COPY --from=builder /app/target/backend-1.0.0-SNAPSHOT.jar /app/hyperlofy-backend.jar
RUN chown -R hyperlofy:hyperlofy /app

# Switch to non-root user
USER hyperlofy

# Container Port Exposure
EXPOSE 8080 8081

# Container Health Check
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Environment Defaults & Tuned JVM Ergonomics
ENV JAVA_OPTS="-Xms512m -Xmx2048m -XX:+UseG1GC -XX:+UseStringDeduplication -Duser.timezone=Asia/Kolkata"

# Entrypoint Execution
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/hyperlofy-backend.jar"]
