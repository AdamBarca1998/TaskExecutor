FROM eclipse-temurin:17-jdk-alpine
COPY target/*.jar TaskDemo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","TaskDemo.jar"]