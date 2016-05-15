#!/bin/bash

CMR_ADDR=${INSPECTIT_CMR_ADDR:-cmr}
CMR_PORT=${INSPECTIT_CMR_PORT:-9070}
AGENT_NAME=${AGENT_NAME:-$HOSTNAME}
TOMCAT_ADRESS=${TOMCAT_PORT_8080_TCP_ADDR:-tomcat}

if [ "$(ls -A $INSPECTIT_CONFIG_HOME)" ]; then

	echo "Using existing inspectIT configuration..."
	CONFIG_PATH=$INSPECTIT_CONFIG_HOME/

else

	if ([ -z $INSPECTIT_CMR_ADDR ] || [ -z $INSPECTIT_CMR_PORT ]) && ([ -z $CMR_PORT_9070_TCP_ADDR ] || [ -z $CMR_PORT_9070_TCP_PORT ]); then

                echo "No inspectIT CMR configured! Please read our README"

                exit 1

        fi


	echo "No custom inspectIT configuration found, using default one..."

	sed -i "s/^\(repository\) .*/\1 $CMR_ADDR $CMR_PORT $AGENT_NAME/" /agent-config/inspectit-agent.cfg
fi
exec java -javaagent:agent/inspectit-agent.jar -Dinspectit.config=$CONFIG_PATH -Deureka.serviceUrl.default=http://$TOMCAT_ADRESS:8080/eureka/v2/ -jar rss-middletier/build/libs/rss-middletier*SNAPSHOT.jar
