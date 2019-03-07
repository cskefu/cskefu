FROM ubuntu:16.04
MAINTAINER Hai Liang Wang <hain@chatopera.com>

ARG VCS_REF

LABEL org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://github.com/chatopera/cosin"

COPY 163.source.list /etc/apt/sources.list

# Set timezone, install other sw as well
RUN apt-get update && \
   apt-get install --no-install-recommends -y tzdata openjdk-8-jdk maven && \ 
   ln -sf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime && \
   dpkg-reconfigure -f noninteractive tzdata && \
   rm -rf /var/lib/apt/lists/*

# Set the locale
ENV LANG C.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL C.UTF-8

# set ENVs
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV MAVEN_HOME=/usr/share/maven
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
    rm -rf /root/.m2 && \
    dpkg -P maven

WORKDIR /opt/chatopera
EXPOSE 8030-8050
CMD ["java", "-jar", "contact-center-3.9.0.war"]

