# Multistage to unzip the war file and set correct permissions on the data directories
FROM debian:bookworm-slim as builder

RUN mkdir -p /geowebcache
COPY var/lib/jetty/webapps/geowebcache.war /

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    unzip

RUN unzip -d /geowebcache /geowebcache.war

FROM jetty:9-jre11 as geowebcache

ENV GEOWEBCACHE_CONFIG_DIR=/mnt/geowebcache_datadir \
    GEOWEBCACHE_CACHE_DIR=/mnt/geowebcache_tiles

RUN java -jar "$JETTY_HOME/start.jar" --create-startd --add-to-start=jmx,jmx-remote,stats,http-forwarded

COPY --chown=jetty:jetty . /

# Temporary switch to root
USER root

COPY --from=builder --chown=jetty:jetty /geowebcache /var/lib/jetty/webapps/geowebcache

RUN mkdir /mnt/geowebcache_tiles /mnt/geowebcache_datadir &&       \
    chown jetty:jetty /etc/georchestra /mnt/geowebcache_tiles /mnt/geowebcache_datadir

# restore jetty user
USER jetty

VOLUME [ "/mnt/geowebcache_datadir", "/mnt/geowebcache_tiles", "/tmp", "/run/jetty" ]

ENTRYPOINT [ "/docker-entrypoint.sh" ]

CMD ["sh", "-c", "exec java \
-Djava.io.tmpdir=/tmp/jetty \
-Dgeorchestra.datadir=/etc/georchestra \
-XX:MaxRAMPercentage=80 -XX:+UseParallelGC \
-XX:-UsePerfData \
${JAVA_OPTIONS} \
-Djetty.httpConfig.sendServerVersion=false \
-Djetty.jmxremote.rmiregistryhost=${JMX_HOST:-127.0.0.1} \
-Djetty.jmxremote.rmiserverhost=${JMX_HOST:-127.0.0.1} \
-Dcom.sun.management.jmxremote=${JMX_ENABLE:-false} \
-Dcom.sun.management.jmxremote.host=${JMX_HOST:-127.0.0.1} \
-Dcom.sun.management.jmxremote.ssl=${JMX_SSL:-false} \
-Dcom.sun.management.jmxremote.authenticate=${JMX_AUTHENTICATE:-false} \
-jar /usr/local/jetty/start.jar"]
