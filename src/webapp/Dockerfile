FROM maven as builder 

COPY . /usr/src/poc
WORKDIR /usr/src/poc
RUN mvn clean && mvn package

FROM openjdk:9-b130

ENV exploit=true

USER 1001

ENV SECRET_VALUE='if you can read this this code is vulnerable'

COPY --from=builder /usr/src/poc/target/webapp-1.0-jar-with-dependencies.jar /opt/app-root/src/webapp-1.0.jar
