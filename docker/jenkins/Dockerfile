FROM jenkins/jenkins:lts-jdk17

USER root

# Install Docker CLI using the official convenience script
# This is more robust across different Debian versions
RUN curl -fsSL https://get.docker.com | sh

# Add jenkins user to docker group
# Note: The group ID (999) might need to match your host's docker group ID
# We will handle permissions via the docker socket mount
RUN usermod -aG docker jenkins

USER jenkins