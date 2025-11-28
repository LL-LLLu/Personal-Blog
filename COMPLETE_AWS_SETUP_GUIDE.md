# 🚀 The Complete AWS Deployment Guide for Weblog

This guide provides a comprehensive, step-by-step process for deploying the full Weblog application stack (Vue.js frontend, Spring Boot backend, MySQL, and MinIO) on a single AWS EC2 instance. It consolidates and corrects all previous AWS-related documentation into a single, authoritative source.

## 📋 Table of Contents

1.  [**EC2 Instance Setup**](#-ec2-instance-setup): Creating and configuring the virtual server.
2.  [**Initial Server Configuration**](#-initial-server-configuration): Connecting and preparing the OS.
3.  [**Software Installation**](#-software-installation): Installing Docker, Nginx, and other required tools.
4.  [**Directory and Security Setup**](#-directory-and-security-setup): Structuring the filesystem and hardening the server.
5.  [**Jenkins CI/CD Setup**](#-jenkins-cicd-setup): Installing and configuring Jenkins on the EC2 instance for automated deployments.
6.  [**Database and Storage Setup**](#-database-and-storage-setup): Deploying MySQL and MinIO containers.
7.  [**Application Deployment**](#-application-deployment): Configuring Jenkins jobs to build and deploy the frontend and backend.
8.  [**S3 Bucket Setup (Optional)**](#-s3-bucket-setup-optional): A production-ready alternative to MinIO.
9.  [**Maintenance and Monitoring**](#-maintenance-and-monitoring): Scripts and best practices for keeping the server healthy.
10. [**Troubleshooting**](#-troubleshooting): Solutions for common issues.

---

## 1. EC2 Instance Setup

### 1.1. Launch an EC2 Instance

1.  **Navigate to the AWS EC2 Console** and click **Launch Instance**.
2.  **Configure the instance** with the following settings:
    *   **Name**: `weblog-production-server`
    *   **Application and OS Images**: `Ubuntu Server 22.04 LTS` (Free Tier eligible)
    *   **Architecture**: `64-bit (x86)`
    *   **Instance Type**: `t2.micro` (Free Tier) for testing, or `t3.small` / `t3.medium` for better performance in production.
    *   **Key Pair**:
        *   Create a new key pair with the name `weblog-server-key`.
        *   Type: **RSA**, Format: **.pem**.
        *   **Crucially, download and save this `.pem` file securely.**
    *   **Network Settings**:
        *   Click **Edit**.
        *   **VPC**: Use the default VPC.
        *   **Auto-assign public IP**: `Enable`.
        *   **Security Group**: Create a new one named `weblog-sg`.
        *   **Inbound Security Group Rules**:
            | Type | Protocol | Port Range | Source | Description |
            | :--- | :--- | :--- | :--- | :--- |
            | SSH | TCP | 22 | Your IP | For secure shell access |
            | HTTP | TCP | 80 | Anywhere | For web traffic |
            | HTTPS | TCP | 443 | Anywhere | For secure web traffic |
            | Custom TCP | TCP | 8080 | Anywhere | For the Spring Boot backend |
            | Custom TCP | TCP | 8081 | Your IP | For the Jenkins dashboard |
            | Custom TCP | TCP | 9000 | Anywhere | For MinIO API |
            | Custom TCP | TCP | 9001 | Your IP | For MinIO Console |
    *   **Storage (EBS)**:
        *   `30 GiB` of `gp3` storage is a good starting point for production.

3.  Click **Launch instance**.

### 1.2. Associate an Elastic IP (Recommended)

To get a permanent public IP address that doesn't change on instance reboot:
1.  Navigate to **Elastic IPs** in the EC2 dashboard.
2.  **Allocate** a new Elastic IP address.
3.  **Associate** this new IP with your `weblog-production-server` instance.

---

## 2. Initial Server Configuration

### 2.1. Connect to the Server

On your local machine, open a terminal and run the following commands:

```bash
# Restrict permissions of your private key
chmod 400 /path/to/your/weblog-server-key.pem

# Connect to the instance via SSH
ssh -i /path/to/your/weblog-server-key.pem ubuntu@YOUR_ELASTIC_IP
```

### 2.2. System Update and Swap File

Once connected, prepare the system:

```bash
# Update all system packages
sudo apt update && sudo apt upgrade -y

# Install essential tools
sudo apt install -y curl wget git unzip software-properties-common apt-transport-https ca-certificates gnupg lsb-release htop

# Create and enable a 2GB swap file for better performance on small instances
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

---

## 3. Software Installation

### 3.1. Install Docker and Docker Compose

We will use the official Docker repository to ensure we get the latest stable version.

```bash
# Add Docker's official GPG key
sudo mkdir -m 0755 -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Set up the Docker repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine, CLI, containerd, and Compose plugin
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Add the 'ubuntu' user to the 'docker' group to run Docker commands without sudo
sudo usermod -aG docker ubuntu

# Verify installation (you may need to log out and log back in for the group change to apply)
docker --version
docker compose version
```
*After running `usermod`, log out and SSH back in to activate the group permission.*

### 3.2. Install Nginx

Nginx will act as a reverse proxy, serving our frontend and directing API traffic to the backend.

```bash
# Install Nginx
sudo apt install -y nginx

# Start and enable Nginx to run on boot
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 3.3. Install Java, Maven, and Node.js (for Jenkins)

These tools are required by Jenkins to build the project.

```bash
# Install OpenJDK 11 (for Jenkins and Spring Boot)
sudo apt install -y openjdk-11-jre-headless

# Install Maven (for building the backend)
sudo apt install -y maven

# Install Node.js 18.x LTS (for building the frontend)
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Verify installations
java -version
mvn -version
node -v
npm -v
```

---

## 4. Directory and Security Setup

### 4.1. Create Application Directory Structure

Create a clean folder structure to hold application artifacts, deployment scripts, and persistent data.

```bash
# Create parent directories
sudo mkdir -p /app/weblog
sudo mkdir -p /docker/nginx/html
sudo mkdir -p /docker/data/mysql
sudo mkdir -p /docker/data/minio

# Set ownership to the 'ubuntu' user
sudo chown -R ubuntu:ubuntu /app
sudo chown -R ubuntu:ubuntu /docker
```

### 4.2. Configure Nginx

Set up Nginx to serve the Vue.js frontend and proxy API requests.

```bash
# Create a new Nginx configuration file for the weblog
sudo tee /etc/nginx/sites-available/weblog.conf > /dev/null <<'EOF'
server {
    listen 80;
    server_name YOUR_ELASTIC_IP_OR_DOMAIN; # Replace with your IP or domain

    root /docker/nginx/html;
    index index.html;

    # Route for the backend API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Route for all frontend requests (to support Vue Router)
    location / {
        try_files $uri $uri/ /index.html;
    }
}
EOF

# Enable the new site by creating a symbolic link
sudo ln -s /etc/nginx/sites-available/weblog.conf /etc/nginx/sites-enabled/

# Remove the default Nginx configuration
sudo rm /etc/nginx/sites-enabled/default

# Test the Nginx configuration and reload
sudo nginx -t
sudo systemctl reload nginx
```

### 4.3. Harden Server Security (UFW & Fail2Ban)

```bash
# Install Uncomplicated Firewall (UFW)
sudo apt install -y ufw
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow http
sudo ufw allow https
sudo ufw allow 8080/tcp # Backend
sudo ufw allow 8081/tcp # Jenkins
sudo ufw allow 9000/tcp # MinIO API
sudo ufw allow 9001/tcp # MinIO Console
sudo ufw --force enable

# Install Fail2Ban to prevent brute-force attacks
sudo apt install -y fail2ban
sudo systemctl start fail2ban
sudo systemctl enable fail2ban
```

---

## 5. Jenkins CI/CD Setup

Installing Jenkins directly on the server simplifies the deployment pipeline significantly by removing the need for SSH credentials.

### 5.1. Install Jenkins

```bash
# Add the Jenkins repository key
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null

# Add the Jenkins repository to the system
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

# Install Jenkins
sudo apt update
sudo apt install -y jenkins

# Change Jenkins default port to 8081 to avoid conflict with the backend
sudo sed -i 's/HTTP_PORT=8080/HTTP_PORT=8081/' /etc/default/jenkins

# Start and enable Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Add jenkins user to the docker group to allow it to run docker commands
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### 5.2. Initial Jenkins Setup

1.  **Retrieve Admin Password**: Get the auto-generated password from the server:
    ```bash
    sudo cat /var/lib/jenkins/secrets/initialAdminPassword
    ```
2.  **Access Jenkins Dashboard**: Open your browser and navigate to `http://YOUR_ELASTIC_IP:8081`.
3.  **Unlock Jenkins**: Paste the password you retrieved.
4.  **Customize Jenkins**: Click **Install suggested plugins**.
5.  **Create Admin User**: Create your own admin account.

Your Jenkins instance is now ready.

---

## 6. Database and Storage Setup

We will use Docker to run MySQL and MinIO, ensuring a consistent and isolated environment.

### 6.1. Deploy MySQL Container

```bash
docker run -d \
  --name weblog-mysql \
  --restart=always \
  -p 3306:3306 \
  -v /docker/data/mysql:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=YOUR_STRONG_MYSQL_ROOT_PASSWORD \
  -e MYSQL_DATABASE=weblog \
  -e MYSQL_USER=weblog \
  -e MYSQL_PASSWORD=YOUR_STRONG_MYSQL_USER_PASSWORD \
  mysql:8.0 \
  --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```
**Note**: Replace the placeholder passwords with strong, unique passwords.

### 6.2. Deploy MinIO Container

```bash
docker run -d \
  --name weblog-minio \
  --restart=always \
  -p 9000:9000 \
  -p 9001:9001 \
  -v /docker/data/minio:/data \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=YOUR_STRONG_MINIO_PASSWORD \
  minio/minio server /data --console-address ":9001"
```
**Note**: Replace the placeholder password.

### 6.3. Create MinIO Bucket

After the container starts, create the `weblog` bucket.

```bash
# Wait a few seconds for MinIO to initialize
sleep 10

# Set up the MinIO client alias
docker exec weblog-minio mc alias set local http://localhost:9000 minioadmin YOUR_STRONG_MINIO_PASSWORD

# Create the 'weblog' bucket
docker exec weblog-minio mc mb local/weblog

# Set the bucket policy to allow public reads (for images)
docker exec weblog-minio mc anonymous set public local/weblog
```

---

## 7. Application Deployment

Now, configure Jenkins jobs to automatically build and deploy the frontend and backend.

### 7.1. Clone the Project

First, clone your project into the `/app/weblog` directory on the server so Jenkins can access it.

```bash
git clone https://github.com/LL-LLLu/Personal-Blog.git /app/weblog
sudo chown -R jenkins:jenkins /app/weblog
```

### 7.2. Create the Backend Dockerfile

Create a `Dockerfile` in `/app/weblog/weblog/weblog-springboot` for the backend.

```bash
sudo tee /app/weblog/weblog/weblog-springboot/Dockerfile > /dev/null <<'EOF'
# Use a build stage to compile the Java application
FROM maven:3.8-openjdk-11 AS builder
WORKDIR /build
COPY pom.xml .
COPY weblog-web/pom.xml weblog-web/
COPY weblog-module-admin/pom.xml weblog-module-admin/
COPY weblog-module-common/pom.xml weblog-module-common/
COPY weblog-module-jwt/pom.xml weblog-module-jwt/
COPY weblog-module-search/pom.xml weblog-module-search/
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Use a slim JRE for the final production image
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /build/weblog-web/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
EOF
```

### 7.3. Configure the Frontend Jenkins Job

1.  In Jenkins, click **New Item**, name it `weblog-frontend`, choose **Freestyle project**, and click **OK**.
2.  **Source Code Management**: Select **Git**.
    *   **Repository URL**: `/app/weblog` (This uses the local clone).
    *   **Branch Specifier**: `*/main`.
3.  **Build Triggers**: Select **Poll SCM** and set the schedule to `H/5 * * * *` to check for changes every 5 minutes.
4.  **Build Steps**: Add an **Execute shell** step with the following command:
    ```bash
    # Navigate to the frontend directory
    cd blog-vue3

    # Install dependencies and build the project
    npm install
    npm run build

    # Deploy the built files to Nginx's web root
    sudo rm -rf /docker/nginx/html/*
    sudo cp -r dist/* /docker/nginx/html/

    # Reload Nginx to apply changes
    sudo systemctl reload nginx
    ```
5.  **Save** the job.

### 7.4. Configure the Backend Jenkins Job

1.  In Jenkins, click **New Item**, name it `weblog-backend`, choose **Freestyle project**, and click **OK**.
2.  **Source Code Management**: Select **Git**.
    *   **Repository URL**: `/app/weblog`.
    *   **Branch Specifier**: `*/main`.
3.  **Build Steps**: Add an **Execute shell** step with the following command:
    ```bash
    # Define image and container names
    IMAGE_NAME="weblog-backend:latest"
    CONTAINER_NAME="weblog-backend"
    
    # Navigate to the backend directory
    cd weblog/weblog-springboot

    # Build the Docker image using the Dockerfile
    docker build -t $IMAGE_NAME .

    # Stop and remove the old container if it exists
    docker stop $CONTAINER_NAME || true
    docker rm $CONTAINER_NAME || true

    # Run the new container with production settings
    docker run -d \
      --name $CONTAINER_NAME \
      --restart=always \
      -p 8080:8080 \
      -e SPRING_PROFILES_ACTIVE=prod \
      -e SPRING_DATASOURCE_URL=jdbc:mysql://172.17.0.1:3306/weblog?useSSL=false \
      -e SPRING_DATASOURCE_USERNAME=weblog \
      -e SPRING_DATASOURCE_PASSWORD=YOUR_STRONG_MYSQL_USER_PASSWORD \
      -e MINIO_ENDPOINT=http://172.17.0.1:9000 \
      -e MINIO_ACCESS_KEY=minioadmin \
      -e MINIO_SECRET_KEY=YOUR_STRONG_MINIO_PASSWORD \
      --network=bridge \
      $IMAGE_NAME
      
    # Clean up old, dangling Docker images
    docker image prune -f
    ```
    **Note**: Replace the placeholder passwords with the ones you set for MySQL and MinIO. We use `172.17.0.1`, which is the default IP of the Docker host bridge network, to connect from the backend container to the database/MinIO containers.
4.  **Save** the job.

### 7.5. Run the Deployments

Click **Build Now** for both the `weblog-frontend` and `weblog-backend` jobs to perform the initial deployment.

---

## 8. S3 Bucket Setup (Optional)

For a more robust, scalable, and production-ready setup, using AWS S3 for file storage instead of a self-hosted MinIO container is highly recommended.

*Detailed instructions for setting up an S3 bucket, including IAM policies, CORS configuration, and Spring Boot integration, can be found in the `AWS_S3_BUCKET_SETUP_GUIDE.md` file. Follow that guide and update the backend container's environment variables accordingly.*

---

## 9. Maintenance and Monitoring

### 9.1. Automated Backups

Create a script to back up your database and MinIO data.

```bash
sudo tee /usr/local/bin/backup.sh > /dev/null <<'EOF'
#!/bin/bash
BACKUP_DIR="/app/weblog/backups/$(date +%Y-%m-%d)"
mkdir -p $BACKUP_DIR

# Backup MySQL Database
docker exec weblog-mysql mysqldump -u root -pYOUR_STRONG_MYSQL_ROOT_PASSWORD weblog > $BACKUP_DIR/weblog_db.sql

# Backup MinIO Data
tar -czf $BACKUP_DIR/minio_data.tar.gz -C /docker/data/minio .

# Optional: Remove backups older than 7 days
find /app/weblog/backups/ -type d -mtime +7 -exec rm -rf {} \;
EOF

sudo chmod +x /usr/local/bin/backup.sh

# Run this script daily via cron
(crontab -l 2>/dev/null; echo "0 2 * * * /usr/local/bin/backup.sh") | crontab -
```

### 9.2. Monitoring Server Health

Use these commands to check the status of your application and server:

```bash
# Check running Docker containers
docker ps

# View logs for a specific container
docker logs -f weblog-backend
docker logs -f weblog-mysql

# Check CPU and memory usage
htop

# Check disk space
df -h

# Check Nginx and Jenkins service status
sudo systemctl status nginx
sudo systemctl status jenkins
```

---

## 10. Troubleshooting

*   **Permission Denied (Docker)**: If you get a "permission denied" error when running `docker`, you need to log out and log back in after adding your user to the `docker` group.
*   **Jenkins Build Fails**: Check the "Console Output" for the specific build in Jenkins. Common issues include incorrect paths, permissions errors, or failed tests. Ensure the `jenkins` user has the necessary permissions.
*   **Nginx 502 Bad Gateway**: This usually means the backend application is down or not accessible on `localhost:8080`. Check the backend container logs with `docker logs -f weblog-backend`.
*   **Database Connection Fails**: Verify the IP address (`172.17.0.1`), database name, username, and password in the backend's `docker run` command match the MySQL container's setup.
*   **CORS Errors in Browser**: If the frontend can't communicate with the backend, ensure the Nginx `proxy_pass` is configured correctly. If using S3, ensure the CORS policy on the bucket is set up to allow requests from your domain.

---

🎉 **Your Weblog application is now fully deployed on AWS with a CI/CD pipeline!**
