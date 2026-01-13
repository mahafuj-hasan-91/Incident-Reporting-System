# 1. Base Image: Use a lightweight Java Runtime (JRE)
FROM eclipse-temurin:21-jre-alpine

# 2. (Optional) Add a volume pointing to /tmp (useful for Tomcat temp files)
#VOLUME /tmp

# 3. Copy the jar file from your target folder to the container
# This assumes there is only one .jar in the target folder
Add target/incident-reporting-system-1.0.0.jar incident-reporting-system-1.0.0.jar

# 4. Expose the port your application runs on (default Spring Boot is 8080)
EXPOSE 8080

# 5. Execute the application
ENTRYPOINT ["java", "-jar", "/incident-reporting-system-1.0.0.jar"]