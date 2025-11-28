# 🔧 Jenkins Setup on AWS EC2 Server

## Prerequisites
- AWS EC2 instance (Ubuntu 22.04)
- Security group ports: 22, 80, 443, 8080, 8081 (for Jenkins)
- At least 2GB RAM (t2.small or larger recommended)

---

## 📦 Step 1: Install Java (Required for Jenkins)

```bash
# Connect to your AWS server
./connect-aws.sh prod

# Install Java 11
sudo apt update
sudo apt install openjdk-11-jre-headless -y

# Verify Java installation
java -version
```

---

## 🔧 Step 2: Install Jenkins

```bash
# Add Jenkins repository key
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null

# Add Jenkins repository
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Update package list
sudo apt update

# Install Jenkins
sudo apt install jenkins -y

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Check status
sudo systemctl status jenkins
```

---

## 🔑 Step 3: Configure Jenkins Security Group

Add port 8081 to your AWS Security Group:

1. Go to AWS Console → EC2 → Security Groups
2. Select your instance's security group
3. Edit inbound rules
4. Add rule:
   - Type: Custom TCP
   - Port: 8081
   - Source: 0.0.0.0/0 (or your IP for better security)

---

## 🌐 Step 4: Configure Jenkins to Use Port 8081

```bash
# Edit Jenkins configuration
sudo nano /lib/systemd/system/jenkins.service

# Find the line with Environment="JENKINS_PORT=8080"
# Change it to:
# Environment="JENKINS_PORT=8081"

# Or use this command to change it automatically:
sudo sed -i 's/JENKINS_PORT=8080/JENKINS_PORT=8081/g' /lib/systemd/system/jenkins.service

# Reload systemd and restart Jenkins
sudo systemctl daemon-reload
sudo systemctl restart jenkins

# Verify it's running on port 8081
sudo netstat -tlpn | grep 8081
```

---

## 🚀 Step 5: Access Jenkins Web Interface

1. **Get initial admin password**:
```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

2. **Access Jenkins**:
   - Open browser: `http://YOUR_AWS_IP:8081`
   - Enter the initial admin password
   - Install suggested plugins
   - Create admin user

---

## 📦 Step 6: Install Required Tools

### Install Maven
```bash
sudo apt install maven -y
mvn -version
```

### Install Node.js and npm
```bash
# Install Node.js 18.x
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify installation
node -v
npm -v
```

### Install Git
```bash
sudo apt install git -y
git --version
```

---

## 🔨 Step 7: Configure Jenkins Jobs (Simplified!)

Since Jenkins is on the same server, deployment is MUCH simpler:

### Frontend Job Configuration

**Job Name**: `weblog-frontend`

**Source Code Management**:
- Git URL: `https://github.com/LL-LLLu/Personal-Blog.git`
- Branch: `*/main`

**Build Triggers**:
- Poll SCM: `H/5 * * * *` (check every 5 minutes)
- Or setup GitHub webhook

**Build Steps** (Execute shell):
```bash
# Navigate to frontend directory
cd blog-vue3

# Install dependencies and build
npm install
npm run build

# Deploy directly (no SSH needed!)
sudo rm -rf /docker/nginx/html/*
sudo cp -r dist/* /docker/nginx/html/

# Reload nginx
sudo nginx -s reload

echo "Frontend deployed successfully!"
```

### Backend Job Configuration

**Job Name**: `weblog-backend`

**Source Code Management**:
- Git URL: `https://github.com/LL-LLLu/Personal-Blog.git`
- Branch: `*/main`

**Build Steps** (Invoke Maven):
- Goals: `clean package -DskipTests`
- POM: `weblog/weblog-springboot/pom.xml`

**Post-build Actions** (Execute shell):
```bash
# Copy JAR file
cp weblog/weblog-springboot/weblog-web/target/*.jar /app/weblog/

# Copy Dockerfile
cp blog-vue3/weblog/weblog-springboot/weblog-web/Dockerfile /app/weblog/

# Navigate to deployment directory
cd /app/weblog

# Build and restart Docker container
docker build -t weblog-backend:latest .
docker stop weblog-backend || true
docker rm weblog-backend || true

docker run -d \
  --name weblog-backend \
  --restart=always \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/weblog \
  -e SPRING_DATASOURCE_USERNAME=weblog \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e MINIO_ENDPOINT=http://localhost:9000 \
  -e MINIO_ACCESS_KEY=minioadmin \
  -e MINIO_SECRET_KEY=minioadmin \
  weblog-backend:latest

# Cleanup
docker image prune -f

echo "Backend deployed successfully!"
```

---

## 🔐 Step 8: Grant Jenkins Docker Permissions

```bash
# Add Jenkins user to docker group
sudo usermod -aG docker jenkins

# Restart Jenkins to apply group changes
sudo systemctl restart jenkins
```

---

## 🎯 Step 9: Configure Jenkins Global Tools

1. Go to Jenkins → Manage Jenkins → Global Tool Configuration

2. **Maven**:
   - Name: `Maven-3.9`
   - MAVEN_HOME: `/usr/share/maven`

3. **JDK**:
   - Name: `JDK-11`
   - JAVA_HOME: `/usr/lib/jvm/java-11-openjdk-amd64`

4. **Git**:
   - Name: `Default`
   - Path: `git`

---

## 🚀 Step 10: Setup GitHub Webhook (Optional)

For automatic builds on push:

1. Go to your GitHub repository settings
2. Webhooks → Add webhook
3. Payload URL: `http://YOUR_AWS_IP:8081/github-webhook/`
4. Content type: `application/json`
5. Select: Just the push event

---

## ✅ Advantages of This Setup

1. **No SSH configuration needed** - Jenkins runs on the same server
2. **Direct file access** - Can copy files directly without SSH/SCP
3. **Simpler paths** - Use local paths instead of remote paths
4. **Faster deployment** - No network transfer needed
5. **Single server** - Everything on one EC2 instance (cost-effective)
6. **Easier debugging** - All logs in one place

---

## 🛠️ Troubleshooting

### Jenkins won't start:
```bash
# Check logs
sudo journalctl -u jenkins -f

# Check if port is in use
sudo lsof -i:8081
```

### Permission issues:
```bash
# Fix Jenkins permissions
sudo chown -R jenkins:jenkins /var/lib/jenkins
sudo chmod -R 755 /var/lib/jenkins
```

### Can't access Jenkins web UI:
1. Check AWS Security Group has port 8081 open
2. Check Jenkins is running: `sudo systemctl status jenkins`
3. Check firewall: `sudo ufw status`

---

## 📊 Complete Architecture

```
GitHub Repository
    ↓ (webhook/polling)
Jenkins (on AWS EC2:8081)
    ↓ (builds)
    ├── Frontend → /docker/nginx/html (local copy)
    └── Backend → Docker Container (local docker run)

All on same EC2 instance!
```

---

## 🎉 Next Steps

1. Create your first build job
2. Test manual build
3. Setup GitHub webhook for auto-build
4. Configure email notifications (optional)
5. Add build badges to your README

---

**This setup is MUCH simpler than local Jenkins + SSH deployment!**