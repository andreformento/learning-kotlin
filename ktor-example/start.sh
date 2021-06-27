#!/bin/bash
set -e

JAVA_OPTS="${JAVA_OPTS:--Xdebug -Xrunjdwp:transport=dt_socket,address=8877,server=y,suspend=n}"
APP_LOGGING="${APP_LOGGING:--Dlogback.configurationFile=/root/resources/logback.xml}"
APP_CONFIG="${APP_CONFIG:--config=/root/resources/application.conf}"

exec java -server $APP_LOGGING \
	-jar app.jar $APP_CONFIG $*
