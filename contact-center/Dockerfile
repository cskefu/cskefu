FROM ubuntu:18.04
MAINTAINER Hai Liang Wang <hain@chatopera.com>

ARG DEBIAN_FRONTEND=noninteractive
ARG VCS_REF
ARG APPLICATION_CUSTOMER_ENTITY
ARG APPLICATION_BUILD_DATESTR

ENV APPLICATION_CUSTOMER_ENTITY=$APPLICATION_CUSTOMER_ENTITY
ENV APPLICATION_BUILD_DATESTR=$APPLICATION_BUILD_DATESTR

LABEL org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://github.com/chatopera/cosin"

# COPY $PWD/assets/aliyun.sources.list /etc/apt/sources.list
# install amazon jdk corretto
COPY $PWD/assets/install-corretto-8.sh /opt
RUN chmod +x /opt/install-corretto-8.sh && /opt/install-corretto-8.sh

# install other lib and configure timezone
RUN apt-get update && \
   apt-get install --no-install-recommends -y tzdata iputils-ping mysql-client-5.7 zip unzip vim-tiny libfontconfig1 libfreetype6 && \ 
   ln -sf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime && \
   DEBIAN_FRONTEND=noninteractive dpkg-reconfigure --frontend noninteractive tzdata && \
   rm -rf /var/lib/apt/lists/*

# Set the locale
ENV LANG C.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL C.UTF-8

# set ENVs
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-amazon-corretto
ENV PATH=$PATH:$JAVA_HOME/bin

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