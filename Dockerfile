FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy file jar từ target
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
