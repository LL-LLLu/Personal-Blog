# AWS Deployment Guide for Weblog Application

## Prerequisites

- AWS Account with appropriate permissions
- Domain name (optional, for production)
- Local machine with AWS CLI installed
- Your application code ready for deployment

## Step 1: Launch EC2 Instance

### 1.1 Login to AWS Console
1. Go to [AWS Console](https://console.aws.amazon.com)
2. Navigate to **EC2** service
3. Click **Launch Instance**

### 1.2 Configure Instance
```
Name: weblog-server
AMI: Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
Instance Type: t3.medium (minimum recommended)
  - 2 vCPUs
  - 4 GB RAM
  - For production: consider t3.large or larger
```

### 1.3 Key Pair
```
Key pair name: Create new key pair
Key pair type: RSA
Private key format: .pem (for Mac/Linux) or .ppk (for Windows)
Download and save the key file securely
```

### 1.4 Network Settings
```
VPC: Default VPC (or create custom)
Subnet: No preference
Auto-assign public IP: Enable
Security Group: Create new
  - Name: weblog-sg
  - Description: Security group for Weblog application
```

### 1.5 Configure Security Group Rules
Add the following inbound rules:
```
SSH:
  - Type: SSH
  - Port: 22
  - Source: My IP (or 0.0.0.0/0 for anywhere - less secure)

HTTP:
  - Type: HTTP
  - Port: 80
  - Source: 0.0.0.0/0

HTTPS:
  - Type: HTTPS
  - Port: 443
  - Source: 0.0.0.0/0

Backend API (optional, for direct access):
  - Type: Custom TCP
  - Port: 8080
  - Source: 0.0.0.0/0

MinIO Console (optional):
  - Type: Custom TCP
  - Port: 9001
  - Source: My IP
```

### 1.6 Configure Storage
```
Root volume: 30 GB gp3 (minimum)
- For production: 50-100 GB recommended
- Enable encryption: Yes
```

### 1.7 Advanced Details (Optional)
```yaml
User data (paste this to auto-install Docker):
#!/bin/bash
apt-get update
apt-get install -y docker.io docker-compose git
systemctl start docker
systemctl enable docker
usermod -aG docker ubuntu
```

### 1.8 Launch Instance
Click **Launch Instance** and wait for it to start.

## Step 2: Connect to Your EC2 Instance

### 2.1 Set Permissions for Key File
```bash
# On your local machine
chmod 400 your-key-name.pem
```

### 2.2 Connect via SSH
```bash
# Get public IP from EC2 console
ssh -i your-key-name.pem ubuntu@your-ec2-public-ip

# Example:
ssh -i weblog-key.pem ubuntu@52.23.45.67
```

## Step 3: Install Required Software

### 3.1 Update System
```bash
sudo apt-get update
sudo apt-get upgrade -y
```

### 3.2 Install Docker (if not installed via user data)
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installations
docker --version
docker-compose --version

# Logout and login again for group changes
exit
# Then SSH back in
ssh -i your-key-name.pem ubuntu@your-ec2-public-ip
```

### 3.3 Install Additional Tools
```bash
# Install Git, Nginx, Certbot (for SSL)
sudo apt-get install -y git nginx certbot python3-certbot-nginx

# Install MySQL client (optional, for debugging)
sudo apt-get install -y mysql-client
```

## Step 4: Clone and Setup Application

### 4.1 Create Application Directory
```bash
# Create app directory
sudo mkdir -p /opt/weblog
sudo chown -R $USER:$USER /opt/weblog
cd /opt/weblog
```

### 4.2 Clone Your Repository
```bash
# Option 1: If your repo is public
git clone https://github.com/your-username/your-repo.git .

# Option 2: If private, use personal access token
git clone https://YOUR_TOKEN@github.com/your-username/your-repo.git .

# Option 3: Upload files manually using SCP from local machine
# On your local machine:
scp -i your-key-name.pem -r /path/to/docker/* ubuntu@your-ec2-ip:/opt/weblog/
```

### 4.3 Create Directory Structure
```bash
# If you uploaded files manually, organize them:
mkdir -p /opt/weblog/{docker,weblog-springboot,blog-vue3}

# Copy Docker files
cp -r docker/* /opt/weblog/docker/

# Create necessary directories
mkdir -p /opt/weblog/docker/nginx/ssl
mkdir -p /opt/weblog/docker/logs
mkdir -p /opt/weblog/data/{mysql,minio}
```

## Step 5: Configure Application for Production

### 5.1 Update Environment Variables
```bash
cd /opt/weblog/docker

# Create production environment file
cat > .env << 'EOF'
# Database
MYSQL_ROOT_PASSWORD=YourStrongPassword123!
MYSQL_PASSWORD=YourStrongPassword123!

# MinIO
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=YourMinioPassword123!

# Application
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080

# Domain (update with your domain or EC2 public IP)
DOMAIN_NAME=your-domain.com
PUBLIC_IP=your-ec2-public-ip
EOF

# Set proper permissions
chmod 600 .env
```

### 5.2 Update docker-compose.yml for Production
```bash
# Edit docker-compose.yml to use environment variables
nano docker-compose.yml
```

Add these changes:
```yaml
services:
  mysql:
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - /opt/weblog/data/mysql:/var/lib/mysql
  
  minio:
    environment:
      MINIO_ROOT_USER: ${MINIO_ROOT_USER}
      MINIO_ROOT_PASSWORD: ${MINIO_ROOT_PASSWORD}
    volumes:
      - /opt/weblog/data/minio:/data
```

## Step 6: Setup SSL Certificate

### 6.1 For Domain Name (Recommended)
```bash
# Stop nginx if running
sudo systemctl stop nginx

# Get Let's Encrypt certificate
sudo certbot certonly --standalone -d your-domain.com -d www.your-domain.com

# Certificates will be in:
# /etc/letsencrypt/live/your-domain.com/fullchain.pem
# /etc/letsencrypt/live/your-domain.com/privkey.pem

# Copy to Docker nginx directory
sudo cp /etc/letsencrypt/live/your-domain.com/fullchain.pem /opt/weblog/docker/nginx/ssl/cert.pem
sudo cp /etc/letsencrypt/live/your-domain.com/privkey.pem /opt/weblog/docker/nginx/ssl/key.pem
```

### 6.2 For IP Only (Development)
```bash
# Generate self-signed certificate
cd /opt/weblog/docker/nginx/ssl
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout key.pem -out cert.pem \
  -subj "/C=US/ST=State/L=City/O=Organization/CN=your-ec2-public-ip"
```

## Step 7: Build and Deploy Application

### 7.1 Build Spring Boot Application
```bash
cd /opt/weblog/weblog-springboot

# Create Dockerfile if not exists
cat > Dockerfile << 'EOF'
FROM maven:3.8-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY weblog-web/pom.xml weblog-web/
COPY weblog-module-admin/pom.xml weblog-module-admin/
COPY weblog-module-common/pom.xml weblog-module-common/
COPY weblog-module-jwt/pom.xml weblog-module-jwt/
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/weblog-web/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
```

### 7.2 Build Vue.js Application
```bash
cd /opt/weblog/blog-vue3

# Update API endpoint for production
nano .env.production
# Set: VITE_API_BASE_URL=https://your-domain.com/api

# Create Dockerfile if not exists
cat > Dockerfile << 'EOF'
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
EOF
```

### 7.3 Start Services
```bash
cd /opt/weblog/docker

# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

## Step 8: Configure System Services

### 8.1 Create Systemd Service
```bash
# Create service file
sudo nano /etc/systemd/system/weblog.service
```

Add this content:
```ini
[Unit]
Description=Weblog Docker Application
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/opt/weblog/docker
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

### 8.2 Enable Service
```bash
# Reload systemd
sudo systemctl daemon-reload

# Enable service to start on boot
sudo systemctl enable weblog

# Start service
sudo systemctl start weblog

# Check status
sudo systemctl status weblog
```

## Step 9: Setup Monitoring and Backups

### 9.1 Setup CloudWatch Monitoring (Optional)
```bash
# Install CloudWatch agent
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
sudo dpkg -i amazon-cloudwatch-agent.deb

# Configure agent (follow prompts)
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-config-wizard
```

### 9.2 Setup Automated Backups
```bash
# Create backup script
nano /opt/weblog/backup.sh
```

Add this content:
```bash
#!/bin/bash
BACKUP_DIR="/opt/weblog/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p $BACKUP_DIR

# Backup MySQL
docker exec weblog-mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD weblog > $BACKUP_DIR/mysql_$DATE.sql

# Backup MinIO data
tar -czf $BACKUP_DIR/minio_$DATE.tar.gz /opt/weblog/data/minio

# Keep only last 7 days of backups
find $BACKUP_DIR -type f -mtime +7 -delete

# Optional: Upload to S3
# aws s3 cp $BACKUP_DIR/mysql_$DATE.sql s3://your-backup-bucket/
# aws s3 cp $BACKUP_DIR/minio_$DATE.tar.gz s3://your-backup-bucket/
```

```bash
# Make executable
chmod +x /opt/weblog/backup.sh

# Add to crontab (daily at 2 AM)
crontab -e
# Add: 0 2 * * * /opt/weblog/backup.sh
```

## Step 10: Configure Domain and DNS

### 10.1 Route 53 Setup (if using AWS)
1. Go to Route 53 in AWS Console
2. Create Hosted Zone for your domain
3. Create A record pointing to EC2 public IP
4. Create CNAME for www pointing to main domain

### 10.2 External DNS Provider
Update your DNS records:
```
A     @     your-ec2-public-ip
CNAME www   your-domain.com
```

## Step 11: Security Hardening

### 11.1 Configure Firewall
```bash
# Enable UFW firewall
sudo ufw allow OpenSSH
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw --force enable
```

### 11.2 Fail2ban Setup
```bash
# Install fail2ban
sudo apt-get install -y fail2ban

# Configure for SSH protection
sudo cp /etc/fail2ban/jail.conf /etc/fail2ban/jail.local
sudo systemctl restart fail2ban
```

### 11.3 Regular Updates
```bash
# Enable automatic security updates
sudo apt-get install -y unattended-upgrades
sudo dpkg-reconfigure --priority=low unattended-upgrades
```

## Step 12: Performance Optimization

### 12.1 CloudFront CDN (Optional)
1. Go to CloudFront in AWS Console
2. Create Distribution
3. Origin Domain: your-domain.com
4. Cache behaviors for static assets

### 12.2 RDS Database (Optional for Production)
Consider moving to AWS RDS for:
- Automated backups
- High availability
- Automated patches
- Better performance

### 12.3 S3 for Static Assets (Optional)
Move MinIO to S3:
1. Create S3 bucket
2. Configure bucket policy for public read
3. Update application to use S3 instead of MinIO

## Verification Steps

### Test Application
```bash
# Test locally on server
curl http://localhost
curl http://localhost:8080/health

# Test from outside
# Open in browser:
https://your-domain.com (or https://your-ec2-ip)
```

### Check Logs
```bash
# Docker logs
docker-compose logs -f

# System logs
sudo journalctl -u weblog -f

# Nginx logs
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

## Troubleshooting

### Common Issues and Solutions

1. **Cannot connect to EC2**
   - Check security group rules
   - Verify instance is running
   - Check SSH key permissions (400)

2. **Docker permission denied**
   - Run: `sudo usermod -aG docker $USER`
   - Logout and login again

3. **Port already in use**
   - Check: `sudo netstat -tlnp`
   - Stop conflicting service or change port

4. **Database connection failed**
   - Check Docker network: `docker network ls`
   - Verify credentials in .env file
   - Check MySQL logs: `docker logs weblog-mysql`

5. **SSL certificate issues**
   - Verify certificate paths
   - Check certificate validity
   - For Let's Encrypt, ensure domain points to server

6. **Out of memory**
   - Check: `free -h`
   - Consider larger instance type
   - Add swap space:
   ```bash
   sudo fallocate -l 4G /swapfile
   sudo chmod 600 /swapfile
   sudo mkswap /swapfile
   sudo swapon /swapfile
   ```

## Maintenance Commands

```bash
# Update application
cd /opt/weblog
git pull
docker-compose build
docker-compose up -d

# View resource usage
docker stats

# Clean up Docker
docker system prune -a

# Restart services
docker-compose restart

# Database backup
docker exec weblog-mysql mysqldump -u root -p weblog > backup.sql

# View application logs
docker-compose logs -f backend
docker-compose logs -f frontend
```

## Cost Optimization Tips

1. **Use Reserved Instances** for long-term deployments (up to 72% savings)
2. **Use Spot Instances** for development/testing
3. **Enable Auto-scaling** for production
4. **Use CloudWatch** to monitor and right-size instances
5. **Set up billing alerts** in AWS Billing Console

## Support and Resources

- AWS Documentation: https://docs.aws.amazon.com/
- Docker Documentation: https://docs.docker.com/
- EC2 User Guide: https://docs.aws.amazon.com/ec2/
- Security Best Practices: https://aws.amazon.com/security/best-practices/

## Next Steps

1. Set up CI/CD pipeline with AWS CodePipeline or GitHub Actions
2. Implement monitoring with CloudWatch or Prometheus
3. Add autoscaling for high availability
4. Set up staging environment
5. Implement disaster recovery plan