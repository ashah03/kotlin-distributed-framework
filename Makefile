VERSION = 0.1.1

default: stubs build jars docker-build

build:
	./gradlew assemble build

clean:
	./gradlew clean

scripts:
	./gradlew installDist

jars:
	./gradlew -b build.gradle server node

stubs:
	./gradlew clean generateProto

refresh:
	./gradlew --refresh-dependencies dependencies

versioncheck:
	./gradlew dependencyUpdates

server:
	java -jar ./build/libs/kotlin-server.jar

node:
	java -jar ./build/libs/kotlin-node.jar

docker-server:
	docker run --rm -p 50051:50051 server:0.1.1

docker-node:
	docker run --rm node:0.1.1

docker-clean:
	docker container rm $(docker ps -a -q)

docker-build:
	docker build -f ./etc/docker/node.df -t node:${VERSION} .
	docker build -f ./etc/docker/server.df -t server:${VERSION} .
