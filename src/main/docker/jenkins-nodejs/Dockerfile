FROM jenkins/jenkins:lts-jdk17
# if we want to install via apt
USER root
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - &&\
apt-get update && apt-get install -y nodejs && apt-get clean
# drop back to the regular jenkins user - good practice
USER jenkins
