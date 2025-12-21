# 1. Use an OpenJDK 17 runtime
FROM eclipse-temurin:17-jdk-alpine AS builder

# 2. Set working directory
WORKDIR /app

# 3. Copy Gradle build files and source code
COPY build/libs/*.jar app.jar

# 4. Expose the port your Spring Boot app runs on
EXPOSE 8080

# 5. Set timezone (optional)
ENV TZ=Asia/Seoul

# 6. Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
