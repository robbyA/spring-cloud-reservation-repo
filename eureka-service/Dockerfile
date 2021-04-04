FROM openjdk:8-jdk-alpine
EXPOSE 8761
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS=""
#ENTRYPOINT ["java","-Dspring.cloud.config.uri=http://172.16.238.10:8888","-jar","/app.jar"]
#ENTRYPOINT ["java","-jar","/app.jar"]
ENTRYPOINT ["java","-Dspring.cloud.config.uri=http://config-service:8888","-jar","/app.jar"]
#host.docker.internal