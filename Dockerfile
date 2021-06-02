FROM gradle:jdk11 as build
WORKDIR /app
COPY . .
RUN ls -lah
RUN /app/gradlew -p service bootJar

FROM adoptopenjdk/openjdk11:alpine-jre as final
WORKDIR /app

COPY --from=build /app/service/build/libs/*jar /app/
COPY --from=build /app/service/src/main/resources/* /app/
RUN mv /app/fs4r-service*.jar /app/fs4r-service.jar

ENTRYPOINT ["java", "-jar", "/app/fs4r-service.jar"]
