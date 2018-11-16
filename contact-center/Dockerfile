FROM chatopera/java:1.0.0
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


# set ENVs
ENV JAVA_HOME=/opt/java-8-oracle
ENV MAVEN_HOME=/opt/apache-maven
ENV TOMCAT_HOME=/opt/apache-tomcat
ENV PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

# create dirs
RUN /bin/bash -c "mkdir -p /{data,logs}"

# build WAR
COPY app /app
COPY config /config
WORKDIR /app
RUN mvn clean package && \
    rm -rf /opt/apache-tomcat/webapps/ROOT && \
    cp target/contact-center-*.war.original /opt/apache-tomcat/webapps/ROOT.war && \
    rm -rf /app && \
    rm -rf /root/.m2

EXPOSE 8030-8090 9200 9300
CMD ["/opt/apache-tomcat/bin/catalina.sh", "run"]
