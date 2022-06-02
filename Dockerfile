#author: Kevin Palis <kdp44@cornell.edu>, John Palis <johnv.palis@gmail.com>

#use a lightweight image for pre-build to minimize footprint, this only needs to pull repos
FROM alpine/git:v2.30.2 AS pre-build

WORKDIR /toolbox

#create minimal gobii_bundle structure
RUN mkdir -p gobii_bundle/core gobii_bundle/logs gobii_bundle/loaders/gobii_ifl gobii_bundle/extractors/gobii_mde gobii_bundle/loaders/hdf5 gobii_bundle/extractors/hdf5 gobii_connector

#prepare IFL and MDE modules
RUN git clone --progress --depth 1 https://bitbucket.org/ebsproject/gobii-db.git \
    && echo "Gobii-db cloned." \
    && cp -R gobii-db/dal/gobii_ifl/gobii_ifl/* gobii_bundle/loaders/gobii_ifl \
    && cp -R gobii-db/dal/gobii_mde/gobii_mde/* gobii_bundle/extractors/gobii_mde

#prepare middleware scripts
RUN git clone --progress --depth 1 https://bitbucket.org/ebsproject/gobii.scripts.git \
    && echo "Gobii-scripts cloned." \
    && cp -R gobii.scripts/loaders/* gobii_bundle/loaders \
    && cp -R gobii.scripts/extractors/* gobii_bundle/extractors

#prepare hdf5 binaries
RUN git clone --depth 1 https://bitbucket.org/ebsproject/gobii.hdf5.git \
    && echo "Gobii-HDF5 cloned." \
    && cp -R gobii.hdf5/production/bin/fetch* gobii_bundle/extractors/hdf5 \
    && cp -R gobii.hdf5/production/bin/dump* gobii_bundle/extractors/hdf5 \
    && cp -R gobii.hdf5/production/bin/load* gobii_bundle/loaders/hdf5

#prepare gobii connector
RUN git clone --depth 1 https://bitbucket.org/ebsproject/gobii.connector.git \
    && echo "Gobii-Connector cloned."


# gobii-connector build stage
FROM maven:3.6.3-jdk-13 AS gobiiConnectorBuild

ENV maven_thread_count=6

COPY --from=pre-build /toolbox/gobii.connector .

RUN mvn -T $maven_thread_count install -DskipTests


# main build stage
FROM maven:3.6.3-jdk-13 AS build

#copy all build sub-directories
COPY gobii-api-model gobii-api-model
COPY gobii-brapi gobii-brapi
COPY gobii-client gobii-client
COPY gobii-dao gobii-dao
COPY gobii-domain gobii-domain
COPY gobii-dtomapping gobii-dtomapping
COPY gobii-masticator gobii-masticator
COPY gobii-model gobii-model
COPY gobii-process gobii-process
COPY gobii-sampletracking-dao gobii-sampletracking-dao
# COPY gobii-web gobii-web
COPY pom.xml .

#set variables defaults
ENV maven_thread_count=6

####
#RUN mvn clean install
#RUN mvn -Dmaven.test.skip=true install
RUN mvn -T $maven_thread_count install -DskipTests
#RUN mvn clean -f dataflows/integrator -Dmaven.test.skip=true install
#RUN mvn -f dataflows/integratorCompositeApplication -Dmaven.test.skip=true clean package


FROM ubuntu:18.04

#set variables defaults
ENV os_user=gadm
ENV os_pass=g0b11Admin
ENV os_group=gobii
ENV mq_hostname=sm-rabbit

#update and install utility packages
RUN apt-get update -y && apt-get install -y \
        coreutils \
        curl \
        gnupg2 \
        openssh-client \
        openssh-server \
        openssl \
        python-pip \
        python-psycopg2 \
        python2.7-dev \
        python3-pip \
        software-properties-common \
        sudo \
        vim \
        wget

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9 && \
    apt-add-repository 'deb http://repos.azulsystems.com/ubuntu stable main' && \
    apt-get update -y && apt-get install -y zulu-13

RUN pip install --upgrade pip \
    && pip install Numpy pandas \
    && pip3 install pandas==1.1.5

#allow password-based SSH
RUN sed -i "s/#PasswordAuthentication yes/PasswordAuthentication yes/" /etc/ssh/sshd_config

EXPOSE 22
EXPOSE 8080

#Create default user and group. NOTE: change the gadm password on a production system
RUN useradd $os_user -s /bin/bash -m --password $(echo $os_pass | openssl passwd -1 -stdin) && \
    adduser $os_user sudo && \
    groupadd $os_group && \
    usermod -aG $os_group $os_user && \
    usermod -g $os_group $os_user

#copy build results
COPY --from=gobiiConnectorBuild ./target/create_conf.sh /create_conf.sh
COPY --from=gobiiConnectorBuild ./target/gobiiconnector-*.jar /gobiiconnector.jar
COPY --from=pre-build toolbox/gobii_bundle gobii_bundle
COPY --from=build gobii-process/target gobii_bundle/core
RUN chmod +x /gobii_bundle/loaders/gobii_ifl/gobii_ifl.py

RUN /create_conf.sh $mq_hostname

ENTRYPOINT ["java", "-jar", "/gobiiconnector.jar", "connector.conf"]
