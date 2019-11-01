FROM ubuntu:18.04
MAINTAINER Hai Liang Wang <hain@chatopera.com>

ARG DEBIAN_FRONTEND=noninteractive
ARG VCS_REF

LABEL org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://github.com/chatopera/cosin"

# COPY $PWD/assets/aliyun.sources.list /etc/apt/sources.list
# install amazon jdk corretto
COPY $PWD/assets/install-corretto-8.sh /opt
RUN chmod +x /opt/install-corretto-8.sh && /opt/install-corretto-8.sh

# install maven
COPY $PWD/assets/install-maven.sh /opt
RUN chmod +x /opt/install-maven.sh && /opt/install-maven.sh

# configure timezone
RUN apt-get update && \
   apt-get install --no-install-recommends -y tzdata && \ 
   ln -sf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime && \
   DEBIAN_FRONTEND=noninteractive dpkg-reconfigure --frontend noninteractive tzdata && \
   rm -rf /var/lib/apt/lists/*

# Set the locale
ENV LANG C.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL C.UTF-8

# set ENVs
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-amazon-corretto
ENV MAVEN_HOME=/opt/maven
ENV PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

# create dirs
RUN /bin/bash -c "mkdir -p /{data,logs}"

# build WAR
COPY app /app
COPY config /config
WORKDIR /app
RUN mvn clean package && \
    mkdir -p /opt/chatopera && \
    mv target/contact-center-3.9.0.war /opt/chatopera && \
    rm -rf /app && rm -rf /config && \
    rm -rf /root/.m2

WORKDIR /opt/chatopera
EXPOSE 8030-8050
CMD ["java", "-jar", "contact-center-3.9.0.war"]
