FROM chatopera/java:17
MAINTAINER Hai Liang Wang <hain@chatopera.com>

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
RUN mkdir -p /opt/cskefu
COPY ./app/target/contact-center.war /opt/cskefu/contact-center.war
COPY ./assets/mysql.setup.db.sh /opt/cskefu
COPY ./assets/mysql.upgrade.db.sh /opt/cskefu
COPY ./assets/utils.sh /opt/cskefu
COPY ./assets/docker-entrypoint.sh /opt/cskefu
RUN chmod +x /opt/cskefu/*.sh
RUN touch /root/.cskefu.pep

WORKDIR /opt/cskefu
EXPOSE 8030-8050
CMD ["./docker-entrypoint.sh"]
