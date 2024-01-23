FROM openjdk:21-jdk-oracle
LABEL org.opencontainers.image.authors="Sam.Gerstner@samgerstner.pro"
COPY target/EasyExchange-1.0.0.jar EasyExchange-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/EasyExchange-1.0.0.jar"]