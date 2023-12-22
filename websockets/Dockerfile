FROM gradle:latest AS builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build
FROM openjdk:17
COPY --from=builder /home/gradle/src/build/libs/websockets-0.0.1-SNAPSHOT.jar /app/main.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/main.jar"]