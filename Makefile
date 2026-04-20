
deb:
	mvn package deb:package -PdebianPackage -DskipTests -Dfmt.skip=true ${DEPLOY_OPTS}

docker-build:
	mvn clean package docker:build -Pdocker -DdockerImageName=georchestra/geowebcache:latest -DskipTests

war-build:
	mvn clean install -DskipTests -Dfmt.skip=true
