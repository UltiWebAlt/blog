FROM jenkins/jenkins:lts-jdk21
# if we want to install via apt
USER root
RUN curl -fsSL https://deb.nodesource.com/setup_24.x | bash - &&\
apt-get update && apt-get install -y nodejs
# drop back to the regular jenkins user - good practice
USER jenkins
