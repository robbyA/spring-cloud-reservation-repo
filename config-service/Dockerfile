FROM openjdk:11-jre-slim
#FROM openjdk:11
EXPOSE 8888
VOLUME /tmp
ARG JAR_FILE=target/*.jar
ARG PROP_FILES=config/*
COPY ${JAR_FILE} app.jar
#COPY ${PROP_FILES} /tmp/config/
ENTRYPOINT ["java","-jar","/app.jar"]