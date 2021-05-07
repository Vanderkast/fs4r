FROM adoptopenjdk/openjdk11:alpine-jre

COPY /service/build/libs/*jar /
COPY /service/src/main/resources/* /
RUN mv /fs4r-service*.jar /fs4r-service.jar

ENTRYPOINT ["java", "-jar", "/fs4r-service.jar"]
