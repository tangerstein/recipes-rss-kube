FROM java:7
MAINTAINER info.inspectit@novatec-gmbh.de

#declare variable for inspectIT
ENV INSPECTIT_CONFIG_HOME /recipes-rss/agent/config
ENV ARTIFACT_DIR ${ARTIFACT_DIR}
COPY ARTIFACT_DIR /recipes-rss
WORKDIR /recipes-rss

COPY run.sh /run.sh

CMD wget https://github.com/inspectIT/inspectIT/releases/download/${CMR_ENV_INSPECTIT_VERSION}/inspectit-agent-sun1.5.zip; unzip inspectit-agent-sun*.zip; sh /run.sh
