FROM openjdk:19
LABEL authors="raven"

EXPOSE 8080

WORKDIR /app

COPY ./target/CW2_ILP-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]