FROM openjdk:8-jdk-alpine
COPY ["build/libs/testcontainers-stocks.jar", "app.jar"]
ENTRYPOINT ["java", "-jar", "app.jar"]