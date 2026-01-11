# ============================
# Stage 1 – Build the JAR
# ============================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only pom.xml first (dependency caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ============================
# Stage 2 – Runtime Image
# ============================
FROM eclipse-temurin:21-jre-jammy

# Create non-root user (security best practice)
RUN useradd -m appuser

WORKDIR /app

# Copy only the final JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown appuser:appuser app.jar

# Run as non-root
USER appuser

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
