FROM maven as builder 

COPY . /usr/src/poc
WORKDIR /usr/src/poc
RUN mvn clean && mvn package

FROM openjdk:9-b130

ENV exploit=false

USER 1001

COPY --from=builder /usr/src/poc/target/gateway-1.0-jar-with-dependencies.jar /deployments/gateway-1.0-jar-with-dependencies.jar
