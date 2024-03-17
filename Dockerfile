FROM openjdk:17

ARG JAR_FILE_PATH=build/libs/*.jar

WORKDIR /apps

COPY $JAR_FILE_PATH app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
