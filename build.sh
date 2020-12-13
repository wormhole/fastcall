#!/bin/bash

cd fastcall-parent
call mvn clean install
cd ../fastcall-core
call mvn clean install
cd ../fastcall-spring-boot-autoconfigure
call mvn clean install
cd ../fastcall-spring-boot-starter
call mvn clean install
cd ../fastcall-demo-api
call mvn clean install
cd ../fastcall-demo-provider
call mvn clean install
cd ../fastcall-demo-consumer
call mvn clean install
cd ..