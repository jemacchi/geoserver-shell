#!/bin/bash
mvn clean install
docker build -t jemacchi/geoserver-shell:'0.4.2-SNAPSHOT' .
