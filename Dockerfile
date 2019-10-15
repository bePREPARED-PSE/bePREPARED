FROM openjdk:11
ADD target/beprepared.jar beprepared.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "beprepared.jar"]
