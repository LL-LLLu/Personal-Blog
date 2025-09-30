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
- AWS EC2 instance (Ubuntu 20.04 LTS recommended)
- Docker installed on AWS server
- Nginx installed for frontend static files
- Security groups configured for ports 22, 80, 8080

### Initial Server Configuration

```bash
# SSH into your AWS server
ssh -i your-key.pem ubuntu@your-aws-ip

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
sudo apt install docker.io -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ubuntu

# Install Nginx
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx

# Create application directories
sudo mkdir -p /app/weblog
sudo mkdir -p /docker/nginx/html
sudo chown -R ubuntu:ubuntu /app/weblog
sudo chown -R ubuntu:ubuntu /docker/nginx/html

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

## 🎯 Jenkins Frontend Pipeline (Vue.js)

### Step 1: Create Frontend Job

1. **Access Jenkins**: `http://localhost:8081`
2. **Create New Job**:
   - Click "New Item"
   - Name: `weblog-vue3`
   - Type: "Freestyle project"
   - Description: "Frontend Vue.js deployment to AWS Nginx"

### Step 2: Configure Git Source Control

**Source Code Management**:
- Repository URL: `https://github.com/LL-LLLu/Personal-Blog.git`
- Branch: `*/main`
- Credentials: Use existing or create new

### Step 3: Install NodeJS Plugin

1. **Plugin Manager**:
   - Go to "Manage Jenkins" → "Plugin Manager"
   - Available plugins → Search "nodejs"
   - Install "NodeJS Plugin"
   - Restart Jenkins after installation

### Step 4: Configure NodeJS Globally

1. **Global Tool Configuration**:
   - "Manage Jenkins" → "Global Tool Configuration"
   - Find "NodeJS installations"
   - Add NodeJS:
     - Name: `NodeJS-21.5.0`
     - Check "Install automatically"
     - Version: `NodeJS 21.5.0`
   - Save configuration

### Step 5: Configure Frontend Build

**Build Environment**:
- Check "Provide Node & npm bin/ folder to PATH"
- NodeJS Installation: Select `NodeJS-21.5.0`

**Build Steps**:
- Add build step → "Execute shell"
- Commands:
```bash
cd /var/jenkins_home/workspace/weblog-vue3/blog-vue3
npm install
npm run build
```

### Step 6: Configure Frontend Deployment

**Post-build Actions**:
- Add post-build action → "Send files or execute commands over SSH"
- Configuration:
  - SSH Server: `AWS-Weblog-Server`
  - Source files: `blog-vue3/dist/**/*`
  - Remove prefix: `blog-vue3/dist`
  - Remote directory: `docker/nginx/html`

---

## 🐳 Jenkins Backend Pipeline (Spring Boot + Docker)

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
IMAGE_NAME=weblog-web:0.0.1-SNAPSHOT

cd /app/weblog
chmod +x Dockerfile
docker build -t $IMAGE_NAME .
docker rm -f weblog-web
docker run --restart=always -d -p 8080:8080 -e "ARGS=--spring.profiles.active=prod" -v /app/weblog:/app/weblog --name weblog-web $IMAGE_NAME
docker images|grep none|awk '{print $3 }'|xargs docker rmi
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