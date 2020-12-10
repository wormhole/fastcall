cd fastcall-parent
call mvn clean
call mvn install
cd ../fastcall-core
call mvn clean
call mvn install
cd ../fastcall-spring-boot-autoconfigure
call mvn clean
call mvn install
cd ../fastcall-spring-boot-starter
call mvn clean
call mvn install
cd ../fastcall-demo-api
call mvn clean
call mvn install
cd ../fastcall-demo-provider
call mvn clean
call mvn install
cd ../fastcall-demo-consumer
call mvn clean
call mvn install
cd ..