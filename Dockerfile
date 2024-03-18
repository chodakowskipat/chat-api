FROM gradle:8.5.0-jdk17-alpine as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM amazoncorretto:17-alpine

EXPOSE 8080

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/chatAPI-0.0.1-SNAPSHOT.jar /app/chatAPI/server.jar

ENTRYPOINT ["java", "-jar", "/app/chatAPI/server.jar"]
