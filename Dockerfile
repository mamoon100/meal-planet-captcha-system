FROM eclipse-temurin:17.0.3_7-jre
RUN mkdir -p /app/
COPY target/*.jar /app/application.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/application.jar"]
