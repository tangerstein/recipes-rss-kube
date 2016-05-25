#!/bin/bash
./gradlew clean build
cp -R ${SNAP_WORKING_DIR} ${ARTIFACT_DIR}
