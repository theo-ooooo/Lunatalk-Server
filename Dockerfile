FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY lunatalk-api/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
