FROM chatopera/java:17

MAINTAINER Hai Liang Wang <h@cskefu.com>
# base image is built with config/base/build.sh

ARG DEBIAN_FRONTEND=noninteractive
ARG VCS_REF
ARG APPLICATION_CUSTOMER_ENTITY
ARG APPLICATION_BUILD_DATESTR

ENV APPLICATION_CUSTOMER_ENTITY=$APPLICATION_CUSTOMER_ENTITY
ENV APPLICATION_BUILD_DATESTR=$APPLICATION_BUILD_DATESTR

LABEL org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://github.com/cskefu/cskefu"

RUN rm -rf /opt/cskefu && mkdir /opt/cskefu
COPY ./target/serving-api.jar /opt/cskefu/serving-api.jar
COPY ./target/internal-bundles /opt/cskefu/internal-bundles
COPY ./assets/*.sh /opt/cskefu
RUN chmod +x /opt/cskefu/*.sh

WORKDIR /opt/cskefu
EXPOSE 6500-6700
CMD ["./docker-entrypoint.sh"]
