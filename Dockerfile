#author: Kevin Palis <kdp44@cornell.edu>, John Palis <johnv.palis@gmail.com>

from alpine/git:v2.30.2 as pre-build
WORKDIR /gobii_bundle
RUN git clone --progress --verbose https://bitbucket.org/ebsproject/gobii-db.git && echo "Gobii-db cloned."
RUN ls -lht gobii-db/dal/gobii_ifl/gobii_ifl && sleep 60

#from maven:3.8.1-openjdk-16 as build
#if jdk16 fails, use 3.6.3-jdk-13
from maven:3.6.3-jdk-13 as build

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
COPY gobii-web gobii-web
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
#update and install utility packages
RUN apt-get update -y && apt-get install -y \
 sudo \
 wget \
 openssh-client \
 openssh-server \
 openssl \
 gnupg2 \
 software-properties-common \
 vim
EXPOSE 22

#set variables defaults
ENV os_user=gadm
ENV os_pass=g0b11Admin
ENV os_group=gobii

#copy build results
#COPY --from=build gobii_api_model gobii_api_model
#COPY --from=build gobii_model gobii_model
#COPY --from=build gobii_client gobii_client
COPY --from=build gobii-process/target gobii-process/target
COPY --from=build gobii-web/target gobii-web/target
COPY --from=pre-build gobii_bundle gobii_bundle
#Create default user and group. NOTE: change the gadm password on a production system
RUN useradd $os_user -s /bin/bash -m --password $(echo $os_pass | openssl passwd -1 -stdin) && adduser $os_user sudo && \
groupadd $os_group && \
usermod -aG $os_group $os_user && \
usermod -g $os_group $os_user

#allow password-based SSH
RUN sed -i "s/#PasswordAuthentication yes/PasswordAuthentication yes/" /etc/ssh/sshd_config
RUN service ssh restart

#copy the entrypoint/config file and make sure it can execute
COPY config.sh /root
RUN chmod 755 /root/config.sh

#install Java
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9 && \
apt-add-repository 'deb http://repos.azulsystems.com/ubuntu stable main' && \
apt install -y zulu-13

######

ENTRYPOINT ["/root/config.sh"]
