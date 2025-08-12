
deb:
	./mvnw package deb:package -PdebianPackage -DskipTests -Dfmt.skip=true ${DEPLOY_OPTS}

docker-build:
	mvn clean package docker:build -Pdocker,log4j-logstash,sentry-log4j -DdockerImageName=georchestra/geowebcache:latest -DskipTests

war-build:
	./mvnw clean install -DskipTests -Dfmt.skip=true
