FROM java:openjdk-8-jre-alpine

LABEL "catalogue.project_name"="catalogue-metadata"

ENV SLEEP_TIME 0

# add the ressources
RUN sh -c 'mkdir /root/metadata'
#RUN sh -c 'mkdir /root/metadata/conf'
ADD metadata.jar /root/metadata/metadata.jar
#ADD config.yml /root/metadata/config.yml

RUN sh -c 'touch /root/metadata/metadata.jar'

EXPOSE 4400
CMD echo "The application will start in ${SLEEP_TIME}s..." && \
    sleep ${SLEEP_TIME} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /root/metadata/metadata.jar /root/metadata/config.yml