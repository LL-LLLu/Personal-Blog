FROM jenkins/jenkins:lts-jdk17

USER root

# Install Docker CLI
RUN apt-get update && \
    apt-get -y install apt-transport-https \
      ca-certificates \
      curl \
      gnupg2 \
      software-properties-common && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    add-apt-repository \
      "deb [arch=amd64] https://download.docker.com/linux/debian \
      $(lsb_release -cs) \
      stable" && \
    apt-get update && \
    apt-get -y install docker-ce-cli

# Add jenkins user to docker group (trick to allow access to host docker socket)
# Note: The group ID (999) might need to match your host's docker group ID, 
# but usually granting permissions on the socket mount works in dev.

USER jenkins
