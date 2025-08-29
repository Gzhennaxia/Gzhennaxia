FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/time-manager-1.0.0.jar app.jar

RUN mkdir -p /app/data

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]