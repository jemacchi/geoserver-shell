FROM openjdk:15-jdk-alpine3.11
LABEL maintainer="jose.macchi@gmail.com"

# Geoserver-shell Settings
ENV GEOSERVER_SHELL_HOME /opt/gs-shell

RUN mkdir $GEOSERVER_SHELL_HOME
COPY ./target/geoserver-shell-0.4.2-SNAPSHOT-app/gs-shell-0.4.2-SNAPSHOT/ $GEOSERVER_SHELL_HOME/

CMD ["/opt/gs-shell/bin/gs-shell"]
