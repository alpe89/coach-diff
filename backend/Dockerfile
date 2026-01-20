# =============================================================================
# CoachDiff.ai Backend - Dockerfile
# =============================================================================
#
# Multi-stage build for an optimized Docker image.
#
# STAGE 1 (build): Compile Java code with Maven
# STAGE 2 (runtime): Final image with only JRE and JAR
#
# RESULT:
# - Build image: ~400MB (JDK + Maven + sources)
# - Final image: ~200MB (only JRE + JAR)
#
# USAGE:
#   docker build -t coachdiff-backend .
#   docker run -p 8080:8080 --env-file ../.env coachdiff-backend
#
# =============================================================================

# =============================================================================
# STAGE 1: Build
# =============================================================================
# Use Eclipse Temurin JDK 21 (recommended OpenJDK distribution)
# Alpine Linux for lighter image

FROM eclipse-temurin:21-jdk-alpine AS build

# OCI standard metadata
LABEL org.opencontainers.image.title="CoachDiff.ai Backend"
LABEL org.opencontainers.image.description="AI-powered League of Legends coach"
LABEL org.opencontainers.image.source="https://github.com/yourusername/coach-diff"

# Working directory in container
WORKDIR /app

# Copy Maven files needed to download dependencies
# This layer is cached: if pom.xml doesn't change, dependencies aren't re-downloaded
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (cacheable layer)
# --mount=type=cache: mounts persistent cache for .m2 (much faster on rebuild)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Compile and create JAR
# -DskipTests: tests run in CI, not in Docker build
# -B: batch mode (no interactive output)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -B

# =============================================================================
# STAGE 2: Runtime
# =============================================================================
# Use JRE instead of JDK (smaller, no compiler needed)
# Alpine Linux for minimal size

FROM eclipse-temurin:21-jre-alpine AS runtime

# Create non-root user for security
# (never run containers as root in production)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy JAR from build stage
# The "fat" JAR includes all dependencies (Spring Boot repackage)
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Use non-root user
USER appuser

# Exposed port (documentation, doesn't actually open the port)
EXPOSE 8080

# Health check: verify app responds
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Startup command
# -XX:+UseContainerSupport: JVM respects container memory/CPU limits
# -XX:MaxRAMPercentage=75.0: use max 75% of available RAM
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
