FROM ccr.ccs.tencentyun.com/shared/alpine-java:jdk8-slim
VOLUME /tmp
ADD target/cloud-tracking-service-1.0.0.jar app.jar
EXPOSE 40044
ENTRYPOINT ["java","-jar","/app.jar"]
