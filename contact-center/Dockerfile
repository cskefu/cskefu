FROM tomcat:8.5.35-slim
MAINTAINER Hai Liang Wang <hain@chatopera.com>

ARG VCS_REF

LABEL org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://github.com/chatopera/cosin"

COPY 163.source.list /etc/apt/sources.list

# Set timezone
RUN apt-get update && \
   apt-get install --no-install-recommends -y tzdata && \ 
   ln -sf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime && \
   dpkg-reconfigure -f noninteractive tzdata && \
   rm -rf /var/lib/apt/lists/*

# Set the locale
ENV LANG C.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL C.UTF-8

# create dirs
RUN /bin/bash -c "mkdir -p /{data,logs}"

# copy WAR
RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./app/target/contact-center-3.9.0.war.original /usr/local/tomcat/webapps/ROOT.war
COPY ./config/tomcat.context.xml  /usr/local/tomcat/conf/context.xml

EXPOSE 8030-8090 9200 9300