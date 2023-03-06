FROM maven:3-jdk-8-alpine as builder

RUN mkdir -p /usr/src/app

ADD ./pom.xml /usr/src/app/pom.xml
ADD ./src /usr/src/app/src
ADD ./docker/entry-point.sh /app/entry-point.sh


RUN chmod +x /app/entry-point.sh && \
	sync && \
	cd /usr/src/app && \
	mvn package && \
	cp /usr/src/app/target/*.jar /app/cve-2017-538-example.jar && \
	rm -Rf /usr/src/app

FROM debian:9

COPY --from=builder /app /app

RUN apt update && apt install openjdk-8-jdk -y

WORKDIR /app

ENTRYPOINT ["./entry-point.sh"]

EXPOSE 8080
