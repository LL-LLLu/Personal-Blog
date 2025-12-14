# 🚀 Complete AWS Deployment Guide for Weblog Project

## 📋 Table of Contents
1. [AWS Server Setup](#aws-server-setup)
2. [Jenkins Frontend Pipeline (Vue.js)](#jenkins-frontend-pipeline)
3. [Jenkins Backend Pipeline (Spring Boot + Docker)](#jenkins-backend-pipeline)
4. [Dockerfile Configuration](#dockerfile-configuration)
5. [Complete CI/CD Pipeline](#complete-cicd-pipeline)
6. [Troubleshooting](#troubleshooting)

---

## 🏗️ AWS Server Setup

### Prerequisites
- AWS EC2 instance (Ubuntu 22.04 LTS recommended)
- Docker installed on AWS server
- Nginx installed for frontend static files
- Security groups configured for ports 22, 80, 443, 8080, 9000, 9001

### Initial Server Configuration

```bash
# SSH into your AWS server
ssh -i your-key.pem ubuntu@your-aws-ip

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker (Official Method)
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg lsb-release
sudo mkdir -m 0755 -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo usermod -aG docker ubuntu
# Log out and back in for group changes to take effect

# Install Nginx
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx

# Create application directories
sudo mkdir -p /app/weblog
sudo mkdir -p /app/weblog/logs
sudo mkdir -p /docker/nginx/html
sudo mkdir -p /docker/data/mysql
sudo mkdir -p /docker/data/minio
sudo chown -R ubuntu:ubuntu /app/weblog
sudo chown -R ubuntu:ubuntu /docker

# Configure Nginx
sudo nano /etc/nginx/sites-available/default
```

### Nginx Configuration for Frontend
```nginx
server {
    listen 80 default_server;
    listen [::]:80 default_server;

    root /docker/nginx/html;
    index index.html index.htm;

    server_name _;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Test and reload Nginx
sudo nginx -t
sudo systemctl reload nginx
```

---

## 🎯 Jenkins Installation on AWS Server (RECOMMENDED APPROACH)

> **Note:** Installing Jenkins directly on your AWS server is MUCH EASIER than using local Jenkins with SSH. Everything runs on the same server, so no complex SSH configuration is needed!

### Step 1: Connect to Your AWS Server

```bash
# From your local machine
./connect-aws.sh prod
```

### Step 2: Install Java (Required for Jenkins)

```bash
# Update system
sudo apt update

# Install Java 11
sudo apt install openjdk-11-jre-headless -y

# Verify installation
java -version
# Should show: openjdk version "11.0.x"
```

### Step 3: Install Jenkins

```bash
# Add Jenkins repository key
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null

# Add Jenkins repository
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Update and install Jenkins
sudo apt update
sudo apt install jenkins -y

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

### Step 4: Configure Jenkins Port (Use 8081 to avoid conflict)

```bash
# Change Jenkins port from 8080 to 8081
sudo sed -i 's/JENKINS_PORT=8080/JENKINS_PORT=8081/g' /lib/systemd/system/jenkins.service

# Reload and restart
sudo systemctl daemon-reload
sudo systemctl restart jenkins

# Verify Jenkins is running on port 8081
sudo systemctl status jenkins
```

### Step 5: Open Port 8081 in AWS Security Group

1. Go to AWS Console → EC2 → Security Groups
2. Select your instance's security group
3. Edit inbound rules → Add rule:
   - Type: Custom TCP
   - Port: 8081
   - Source: 0.0.0.0/0
4. Save rules

### Step 6: Access Jenkins Web Interface

```bash
# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
# Copy this password!
```

Now open your browser and go to:
- **URL**: `http://98.82.37.203:8081` (your AWS IP)
- Paste the initial admin password
- Install suggested plugins
- Create your admin user

### Step 7: Install Required Tools on Server

```bash
# Install Maven for backend builds
sudo apt install maven -y

# Install Node.js and npm for frontend builds
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Install Git
sudo apt install git -y

# Verify installations
mvn -version
node -v
npm -v
git --version
```

### Step 8: Grant Jenkins Docker Permissions

```bash
# Add Jenkins user to docker group
sudo usermod -aG docker jenkins

# Restart Jenkins to apply changes
sudo systemctl restart jenkins
```

---

## 🎯 Jenkins Job Configuration (Simplified - No SSH Needed!)

### Frontend Job Setup

1. **Create New Job**:
   - Click "New Item"
   - Name: `weblog-frontend`
   - Select: "Freestyle project"
   - Click OK

2. **Source Code Management**:
   - Select: Git
   - Repository URL: `https://github.com/LL-LLLu/Personal-Blog.git`
   - Branch: `*/main`

3. **Build Triggers**:
   - Check: "Poll SCM"
   - Schedule: `H/5 * * * *` (every 5 minutes)

4. **Build Steps**:
   - Add build step → "Execute shell"
   - Command:
```bash
```

5. **Save** and click "Build Now" to test

### Backend Job Setup

1. **Create New Job**:
   - Click "New Item"
   - Name: `weblog-backend`
   - Select: "Freestyle project"
   - Click OK

2. **Source Code Management**:
   - Select: Git
   - Repository URL: `https://github.com/LL-LLLu/Personal-Blog.git`
   - Branch: `*/main`

3. **Build Steps**:
   - Add build step → "Invoke top-level Maven targets"
   - Maven Version: `Maven-3.9` (or default)
   - Goals: `clean package -DskipTests`
   - POM: `weblog/weblog-springboot/pom.xml`

4. **Post-build Actions**:
   - Add post-build action → "Execute shell"
   - Command:
```bash
# Copy files to deployment directory
cp weblog/weblog-springboot/weblog-web/target/*.jar /app/weblog/
cp blog-vue3/weblog/weblog-springboot/weblog-web/Dockerfile /app/weblog/

# Build Docker image
cd /app/weblog
docker build -t weblog-backend:latest .

# Stop and remove old container
docker stop weblog-backend || true
docker rm weblog-backend || true

# Run new container
docker run -d \
  --name weblog-backend \
  --restart=always \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  weblog-backend:latest

echo "Backend deployed successfully!"
```

5. **Save** and click "Build Now" to test

## 🐳 Alternative: Using Local Jenkins with SSH (More Complex)

### Step 1: Create Dockerfile

First, create a Dockerfile in your `weblog/weblog-springboot/weblog-web/` directory:

```dockerfile
# FROM 指定使用哪个镜像作为基准
FROM openjdk:8-jdk-alpine

# 创建目录, 并设置该目录为工作目录
RUN mkdir -p /weblog
WORKDIR /weblog

# 复制文件到镜像中
COPY weblog-web-0.0.1-SNAPSHOT.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai

# 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms300m -Xmx300m -Djava.security.egd=file:/dev/./urandom"

# 应用参数，可通过 docker run -e "ARGS=" 来设置，如 -e "ARGS=--spring.profiles.active=prod"
ENV ARGS=""

# 暴露 8080 端口
EXPOSE 8080

# 启动后端服务
CMD java ${JAVA_OPTS} -jar app.jar $ARGS
```

### Step 2: Update Backend Job Configuration

**Modify existing `weblog-springboot` job**:

1. **Build Steps remain the same**:
   - Maven build: `clean package -Dmaven.test.skip=true`
   - POM: `weblog/weblog-springboot/pom.xml`

2. **Update Post-build Actions**:

**First SSH Step - Upload JAR**:
- SSH Server: `AWS-Weblog-Server`
- Source files: `weblog/weblog-springboot/weblog-web/target/*.jar`
- Remove prefix: `weblog/weblog-springboot/weblog-web/target`
- Remote directory: `app/weblog`
- Exec command: (leave empty)

**Second SSH Step - Upload Dockerfile and Deploy**:
- SSH Server: `AWS-Weblog-Server`
- Source files: `weblog/weblog-springboot/weblog-web/Dockerfile`
- Remove prefix: `weblog/weblog-springboot/weblog-web`
- Remote directory: `app/weblog`
- Exec command:
```bash
#!/bin/bash
IMAGE_NAME=weblog-backend:latest
CONTAINER_NAME=weblog-backend

cd /app/weblog

# Build Docker image
docker build -t $IMAGE_NAME .

# Stop and remove old container
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# Run new container
docker run -d \
  --name $CONTAINER_NAME \
  --restart=always \
  -p 8080:8080 \
  -e "SPRING_PROFILES_ACTIVE=prod" \
  -e "JAVA_OPTS=-Xms512m -Xmx1g" \
  -v /app/weblog/logs:/app/logs \
  $IMAGE_NAME

# Cleanup dangling images
docker image prune -f
```

---

## 🗄️ Database and MinIO Setup

### MySQL Database Setup

```bash
# Run MySQL container
docker run -d \
  --name weblog-mysql \
  --restart=always \
  -e MYSQL_ROOT_PASSWORD=your_secure_password \
  -e MYSQL_DATABASE=weblog \
  -e MYSQL_USER=weblog \
  -e MYSQL_PASSWORD=your_db_password \
  -p 3306:3306 \
  -v /docker/data/mysql:/var/lib/mysql \
  mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci

# Import initial database (if you have SQL file)
docker exec -i weblog-mysql mysql -u root -p<password> weblog < init.sql
```

### MinIO Object Storage Setup

```bash
# Run MinIO container
docker run -d \
  --name weblog-minio \
  --restart=always \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=your_secure_password \
  -p 9000:9000 \
  -p 9001:9001 \
  -v /docker/data/minio:/data \
  minio/minio server /data --console-address ":9001"

# Create bucket (after MinIO starts)
docker exec weblog-minio mc alias set local http://localhost:9000 minioadmin your_secure_password
docker exec weblog-minio mc mb local/weblog
docker exec weblog-minio mc anonymous set public local/weblog
```

### Update Backend Environment Variables

When running the backend container, include database and MinIO configuration:

```bash
docker run -d \
  --name weblog-backend \
  --restart=always \
  -p 8080:8080 \
  -e "SPRING_PROFILES_ACTIVE=prod" \
  -e "SPRING_DATASOURCE_URL=jdbc:mysql://172.17.0.1:3306/weblog?useSSL=false" \
  -e "SPRING_DATASOURCE_USERNAME=weblog" \
  -e "SPRING_DATASOURCE_PASSWORD=your_db_password" \
  -e "MINIO_ENDPOINT=http://172.17.0.1:9000" \
  -e "MINIO_ACCESS_KEY=minioadmin" \
  -e "MINIO_SECRET_KEY=your_secure_password" \
  -e "MINIO_BUCKET_NAME=weblog" \
  -v /app/weblog/logs:/app/logs \
  weblog-backend:latest
```

---

## 📊 Complete CI/CD Pipeline Configuration

### Pipeline Flow

```
Developer Push → GitHub → Jenkins → AWS Deployment

Frontend Flow:
1. Pull Vue.js code
2. npm install & npm run build
3. Upload dist/ files to Nginx directory
4. Frontend updated automatically

Backend Flow:
1. Pull Spring Boot code
2. Maven package (create JAR)
3. Upload JAR and Dockerfile to AWS
4. Build Docker image
5. Stop old container
6. Start new container with auto-restart
```

### Deployment Scripts

**Frontend Deployment Verification**:
```bash
# Check if files were uploaded
ls -la /docker/nginx/html/
# Should see index.html, assets/, etc.

# Test frontend
curl http://your-aws-ip
```

**Backend Deployment Verification**:
```bash
# Check container status
docker ps | grep weblog-web

# Check application logs
docker logs weblog-web

# Test backend API
curl http://your-aws-ip:8080/api/health
```

### Advanced Configuration

**Auto-restart Policy Benefits**:
- Service automatically restarts if crashed
- Survives server reboots
- Handles high traffic spikes better
- Reduces downtime significantly

**Docker Benefits over JAR deployment**:
- Consistent environment
- Easy scaling
- Auto-restart capabilities
- Better resource isolation
- Simplified deployment process

---

## 🔧 Complete Jenkins Job Configuration Summary

### Frontend Job (weblog-vue3)
```yaml
Job Type: Freestyle project
Source: Git (https://github.com/LL-LLLu/Personal-Blog.git)
Build Environment: NodeJS-21.5.0
Build Steps:
  - Execute Shell:
    cd /var/jenkins_home/workspace/weblog-vue3/blog-vue3
    npm install
    npm run build
Post-build:
  - SSH Upload:
    Source: blog-vue3/dist/**/*
    Target: /docker/nginx/html/
```

### Backend Job (weblog-springboot)
```yaml
Job Type: Freestyle project
Source: Git (https://github.com/LL-LLLu/Personal-Blog.git)
Build Environment: Maven-3.9.9
Build Steps:
  - Maven: clean package -Dmaven.test.skip=true
    POM: weblog/weblog-springboot/pom.xml
Post-build:
  - SSH Upload JAR:
    Source: weblog/weblog-springboot/weblog-web/target/*.jar
    Target: /app/weblog/
  - SSH Upload & Deploy:
    Source: weblog/weblog-springboot/weblog-web/Dockerfile
    Target: /app/weblog/
    Exec: Docker build and run script
```

---

## 🚨 Troubleshooting

### Common Issues & Solutions

**Frontend Issues**:
```bash
# Nginx not serving files
sudo systemctl status nginx
sudo nginx -t
sudo systemctl reload nginx

# Permission issues
sudo chown -R www-data:www-data /docker/nginx/html
sudo chmod -R 755 /docker/nginx/html
```

**Backend Issues**:
```bash
# Docker build fails
docker logs weblog-web
docker images
docker ps -a

# Port conflicts
sudo netstat -tulpn | grep :8080
docker port weblog-web

# Container won't start
docker run -it weblog-web:0.0.1-SNAPSHOT /bin/sh
```

**Jenkins Issues**:
```bash
# Plugin installation fails
docker restart jenkins
docker logs jenkins

# SSH connection fails
ssh -i your-key.pem ubuntu@your-aws-ip
# Check security groups, key permissions
```

### Performance Optimization

**Frontend**:
- Enable Nginx gzip compression
- Configure browser caching
- Use CDN for static assets

**Backend**:
- Optimize JVM memory settings
- Configure application.yml for production
- Set up database connection pooling
- Enable application metrics

### Security Considerations

**AWS Security**:
- Use IAM roles instead of root access
- Configure security groups properly
- Enable CloudWatch monitoring
- Set up log rotation

**Application Security**:
- Use HTTPS (SSL certificates)
- Configure CORS properly
- Implement rate limiting
- Use environment variables for secrets

---

## 🎉 Deployment Complete!

Your complete CI/CD pipeline is now configured with:

✅ **Frontend Pipeline**: Vue.js → Jenkins → Nginx on AWS
✅ **Backend Pipeline**: Spring Boot → Jenkins → Docker on AWS
✅ **Auto-restart**: Docker containers survive crashes
✅ **Monitoring**: Logs and container status
✅ **Scalability**: Easy to scale and update

### Next Steps:
1. Set up webhook triggers for automatic deployment on git push
2. Configure environment-specific configurations
3. Set up monitoring and alerting
4. Implement blue-green deployments
5. Add automated testing in the pipeline

**Your weblog application is now running on AWS with full CI/CD automation!** 🚀