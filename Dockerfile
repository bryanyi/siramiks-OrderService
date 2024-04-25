FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} orderservice.jar

# start the jar file
ENTRYPOINT ["java", "-jar", "/orderservice.jar"]

# expose port
EXPOSE 8081
