FROM ubuntu:18.04
MAINTAINER Hai Liang Wang <hain@chatopera.com>

ARG DEBIAN_FRONTEND=noninteractive

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
