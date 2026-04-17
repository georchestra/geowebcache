
deb:
	mvn package deb:package -PdebianPackage -DskipTests -Dfmt.skip=true ${DEPLOY_OPTS}

docker-build:
	mvn clean package docker:build -Pdocker -DdockerImageName=georchestra/geowebcache:test -DskipTests

war-build:
	mvn clean install -DskipTests -Dfmt.skip=true
