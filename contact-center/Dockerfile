FROM chatopera/java:11
MAINTAINER Hai Liang Wang <hain@chatopera.com>
# base image is built with config/base/build.sh

ARG DEBIAN_FRONTEND=noninteractive
ARG VCS_REF
ARG APPLICATION_CUSTOMER_ENTITY
ARG APPLICATION_BUILD_DATESTR

ENV APPLICATION_CUSTOMER_ENTITY=$APPLICATION_CUSTOMER_ENTITY
ENV APPLICATION_BUILD_DATESTR=$APPLICATION_BUILD_DATESTR

LABEL org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://www.cskefu.com"

# create dirs
RUN /bin/bash -c "mkdir -p /{data,logs}"

# build WAR
RUN mkdir -p /opt/chatopera
COPY ./app/target/contact-center.war /opt/chatopera/contact-center.war
COPY ./assets/mysql.setup.db.sh /opt/chatopera
COPY ./assets/mysql.upgrade.db.sh /opt/chatopera
COPY ./assets/utils.sh /opt/chatopera
COPY ./assets/docker-entrypoint.sh /opt/chatopera
RUN chmod +x /opt/chatopera/*.sh

WORKDIR /opt/chatopera
EXPOSE 8030-8050
CMD ["./docker-entrypoint.sh"]
