#!/bin/bash
./gradlew clean googleJavaFormat gitChangelogTask build -i -Dhttp.socketTimeout=60000 -Dhttp.connectionTimeout=60000
