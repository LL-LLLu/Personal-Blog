# 🌐 AWS Server Setup Guide for Weblog Deployment

## 📋 Table of Contents
1. [AWS EC2 Instance Creation](#aws-ec2-instance-creation)
2. [Initial Server Configuration](#initial-server-configuration)
3. [Docker Installation](#docker-installation)
4. [Nginx Installation and Configuration](#nginx-installation-and-configuration)
5. [Security Configuration](#security-configuration)
6. [Directory Structure Setup](#directory-structure-setup)
7. [Network and Firewall Setup](#network-and-firewall-setup)
8. [Server Monitoring Setup](#server-monitoring-setup)
9. [Backup and Maintenance](#backup-and-maintenance)
10. [Troubleshooting](#troubleshooting)

---

## 🚀 AWS EC2 Instance Creation

### Step 1: Launch EC2 Instance

1. **Log into AWS Console**:
   - Go to [AWS Console](https://aws.amazon.com/console/)
   - Navigate to EC2 service

2. **Launch Instance**:
   - Click "Launch Instance"
   - Choose "Launch instances"

3. **Configure Instance**:

   **Name and Tags**:
   ```
   Name: weblog-production-server
   Environment: production
   Project: weblog
   ```

   **Application and OS Images**:
   ```
   AMI: Ubuntu Server 22.04 LTS (Free Tier eligible)
   Architecture: 64-bit (x86)
   ```

   **Instance Type**:
   ```
   Instance type: t2.micro (Free Tier) or t3.micro (Better performance)
   vCPUs: 1
   Memory: 1 GiB (t2.micro) or 1 GiB (t3.micro)
   ```

   **Key Pair**:
   ```
   Key pair name: weblog-server-key
   Key pair type: RSA
   Private key file format: .pem
   ```
   **⚠️ Important**: Download and save the .pem file securely

4. **Network Settings**:
   ```
   VPC: Default VPC
   Subnet: Default subnet
   Auto-assign public IP: Enable
   ```

   **Security Group Configuration**:
   ```
   Security group name: weblog-sg
   Description: Security group for weblog application

   Inbound Rules:
   - SSH (22): Your IP / 0.0.0.0/0
   - HTTP (80): 0.0.0.0/0
   - HTTPS (443): 0.0.0.0/0
   - Custom TCP (8080): 0.0.0.0/0 (for Spring Boot)
   ```

5. **Storage Configuration**:
   ```
   Storage: 8 GiB gp3 (Free Tier) or 20 GiB for production
   Encryption: Not encrypted (or enable for production)
   ```

6. **Launch Instance**:
   - Review configuration
   - Click "Launch instance"
   - Wait for instance to be in "running" state

---

## 🔧 Initial Server Configuration

### Step 1: Connect to Server

1. **Set Key Permissions** (On your local machine):
   ```bash
   chmod 400 ~/Downloads/weblog-server-key.pem
   ```

2. **Connect via SSH**:
   ```bash
   ssh -i ~/Downloads/weblog-server-key.pem ubuntu@your-ec2-public-ip
   ```

   Replace `your-ec2-public-ip` with your instance's public IP address

### Step 2: Initial System Setup

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install essential tools
sudo apt install -y curl wget git unzip software-properties-common apt-transport-https ca-certificates gnupg lsb-release

# Set timezone (optional)
sudo timedatectl set-timezone Asia/Shanghai

# Create swap file (for small instances)
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# Verify swap
free -h
```

### Step 3: Create Application User (Optional but Recommended)

```bash
# Create weblog user
sudo adduser weblog
sudo usermod -aG sudo weblog

# Switch to weblog user
sudo su - weblog
```

---

## 🐳 Docker Installation

### Step 1: Install Docker

```bash
# Add Docker's official GPG key
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker repository
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Update package index
sudo apt update

# Install Docker Engine
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Start and enable Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add current user to docker group
sudo usermod -aG docker ubuntu
sudo usermod -aG docker weblog  # If you created weblog user

# Verify Docker installation
sudo docker --version
sudo docker run hello-world
```

### Step 2: Configure Docker (Optional)

```bash
# Create Docker daemon configuration
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json > /dev/null <<EOF
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  },
  "storage-driver": "overlay2"
}
EOF

# Restart Docker
sudo systemctl restart docker
```

---

## 🌐 Nginx Installation and Configuration

### Step 1: Install Nginx

```bash
# Install Nginx
sudo apt install -y nginx

# Start and enable Nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# Check status
sudo systemctl status nginx

# Test installation
curl http://localhost
```

### Step 2: Configure Nginx for Weblog

```bash
# Backup default configuration
sudo cp /etc/nginx/sites-available/default /etc/nginx/sites-available/default.backup

# Create weblog configuration
sudo tee /etc/nginx/sites-available/weblog > /dev/null <<'EOF'
server {
    listen 80 default_server;
    listen [::]:80 default_server;

    # Server configuration
    server_name _;
    root /docker/nginx/html;
    index index.html index.htm;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/json
        application/javascript
        application/xml+rss
        application/atom+xml
        image/svg+xml;

    # Frontend routes (Vue.js SPA)
    location / {
        try_files $uri $uri/ /index.html;

        # Cache static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # Backend API routes
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Timeout settings
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # Health check endpoint
    location /health {
        proxy_pass http://127.0.0.1:8080/health;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;

    # Hide Nginx version
    server_tokens off;

    # Custom error pages
    error_page 404 /404.html;
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        root /var/www/html;
    }
}
EOF

# Enable weblog site
sudo ln -sf /etc/nginx/sites-available/weblog /etc/nginx/sites-enabled/weblog
sudo rm -f /etc/nginx/sites-enabled/default

# Test configuration
sudo nginx -t

# Reload Nginx
sudo systemctl reload nginx
```

### Step 3: Configure Log Rotation

```bash
# Create log rotation configuration
sudo tee /etc/logrotate.d/nginx > /dev/null <<'EOF'
/var/log/nginx/*.log {
    daily
    missingok
    rotate 52
    compress
    delaycompress
    notifempty
    create 0644 www-data www-data
    prerotate
        if [ -d /etc/logrotate.d/httpd-prerotate ]; then \
            run-parts /etc/logrotate.d/httpd-prerotate; \
        fi
    endscript
    postrotate
        invoke-rc.d nginx rotate >/dev/null 2>&1
    endscript
}
EOF
```

---

## 🛡️ Security Configuration

### Step 1: Configure UFW Firewall

```bash
# Install UFW (if not installed)
sudo apt install -y ufw

# Set default policies
sudo ufw default deny incoming
sudo ufw default allow outgoing

# Allow specific ports
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp

# Enable firewall
sudo ufw --force enable

# Check status
sudo ufw status verbose
```

### Step 2: Secure SSH Configuration

```bash
# Backup SSH config
sudo cp /etc/ssh/sshd_config /etc/ssh/sshd_config.backup

# Configure SSH security
sudo tee -a /etc/ssh/sshd_config > /dev/null <<'EOF'

# Custom security settings
PermitRootLogin no
PasswordAuthentication no
PubkeyAuthentication yes
X11Forwarding no
MaxAuthTries 3
ClientAliveInterval 300
ClientAliveCountMax 2
EOF

# Restart SSH service
sudo systemctl restart sshd
```

### Step 3: Install Fail2Ban

```bash
# Install Fail2Ban
sudo apt install -y fail2ban

# Create custom configuration
sudo tee /etc/fail2ban/jail.local > /dev/null <<'EOF'
[DEFAULT]
bantime = 3600
findtime = 600
maxretry = 3

[sshd]
enabled = true
port = ssh
filter = sshd
logpath = /var/log/auth.log
maxretry = 3

[nginx-http-auth]
enabled = true
filter = nginx-http-auth
port = http,https
logpath = /var/log/nginx/error.log

[nginx-limit-req]
enabled = true
filter = nginx-limit-req
port = http,https
logpath = /var/log/nginx/error.log
maxretry = 10
EOF

# Start and enable Fail2Ban
sudo systemctl start fail2ban
sudo systemctl enable fail2ban

# Check status
sudo fail2ban-client status
```

---

## 📁 Directory Structure Setup

### Step 1: Create Application Directories

```bash
# Create main application directory
sudo mkdir -p /app/weblog
sudo mkdir -p /app/weblog/logs
sudo mkdir -p /app/weblog/backups

# Create Nginx static files directory
sudo mkdir -p /docker/nginx/html

# Create Jenkins workspace equivalent
sudo mkdir -p /opt/jenkins-workspace

# Set ownership and permissions
sudo chown -R ubuntu:ubuntu /app/weblog
sudo chown -R www-data:www-data /docker/nginx/html
sudo chmod -R 755 /docker/nginx/html
sudo chmod -R 775 /app/weblog

# Add ubuntu user to www-data group
sudo usermod -aG www-data ubuntu
```

### Step 2: Create Directory Structure

```bash
# Final directory structure
tree /app /docker
# Should show:
# /app/
# └── weblog/
#     ├── logs/
#     └── backups/
# /docker/
# └── nginx/
#     └── html/
```

---

## 🔧 Network and Firewall Setup

### Step 1: Configure Security Groups (AWS Console)

1. **Go to EC2 → Security Groups**
2. **Select your weblog-sg**
3. **Edit Inbound Rules**:

```
Rule 1 - SSH:
Type: SSH
Protocol: TCP
Port: 22
Source: Your IP (for security) or 0.0.0.0/0

Rule 2 - HTTP:
Type: HTTP
Protocol: TCP
Port: 80
Source: 0.0.0.0/0

Rule 3 - HTTPS:
Type: HTTPS
Protocol: TCP
Port: 443
Source: 0.0.0.0/0

Rule 4 - Spring Boot:
Type: Custom TCP
Protocol: TCP
Port: 8080
Source: 0.0.0.0/0

Rule 5 - Jenkins (if needed):
Type: Custom TCP
Protocol: TCP
Port: 8081
Source: Your IP only
```

### Step 2: Configure Elastic IP (Optional but Recommended)

```bash
# In AWS Console:
# 1. Go to EC2 → Elastic IPs
# 2. Click "Allocate Elastic IP address"
# 3. Click "Allocate"
# 4. Select the new IP → Actions → Associate Elastic IP address
# 5. Select your instance and associate
```

---

## 📊 Server Monitoring Setup

### Step 1: Install Monitoring Tools

```bash
# Install htop for process monitoring
sudo apt install -y htop

# Install netstat for network monitoring
sudo apt install -y net-tools

# Install iostat for disk monitoring
sudo apt install -y sysstat

# Install log monitoring tools
sudo apt install -y multitail
```

### Step 2: Create Monitoring Scripts

```bash
# Create system monitoring script
sudo tee /usr/local/bin/weblog-status.sh > /dev/null <<'EOF'
#!/bin/bash

echo "=== Weblog Server Status ==="
echo "Date: $(date)"
echo ""

echo "=== System Resources ==="
free -h
echo ""
df -h
echo ""

echo "=== Docker Status ==="
docker ps
echo ""

echo "=== Nginx Status ==="
sudo systemctl status nginx --no-pager -l
echo ""

echo "=== Application Logs (last 10 lines) ==="
if [ -f /app/weblog/app.log ]; then
    tail -10 /app/weblog/app.log
else
    echo "No application logs found"
fi
echo ""

echo "=== Nginx Access Log (last 5 lines) ==="
sudo tail -5 /var/log/nginx/access.log
echo ""

echo "=== Active Network Connections ==="
sudo netstat -tulpn | grep -E "(80|8080|22)"
EOF

# Make script executable
sudo chmod +x /usr/local/bin/weblog-status.sh

# Create alias for easy access
echo "alias weblog-status='/usr/local/bin/weblog-status.sh'" >> ~/.bashrc
source ~/.bashrc
```

### Step 3: Setup Log Monitoring

```bash
# Create log monitoring script
sudo tee /usr/local/bin/weblog-logs.sh > /dev/null <<'EOF'
#!/bin/bash

echo "Real-time log monitoring for Weblog"
echo "Press Ctrl+C to exit"
echo ""

# Monitor multiple log files simultaneously
multitail \
  -l "docker logs -f weblog-web 2>&1" \
  -l "sudo tail -f /var/log/nginx/access.log" \
  -l "sudo tail -f /var/log/nginx/error.log"
EOF

sudo chmod +x /usr/local/bin/weblog-logs.sh
echo "alias weblog-logs='/usr/local/bin/weblog-logs.sh'" >> ~/.bashrc
```

---

## 💾 Backup and Maintenance

### Step 1: Create Backup Scripts

```bash
# Create backup directory
sudo mkdir -p /app/weblog/backups

# Create database backup script (if using database)
sudo tee /usr/local/bin/weblog-backup.sh > /dev/null <<'EOF'
#!/bin/bash

BACKUP_DIR="/app/weblog/backups"
DATE=$(date +"%Y%m%d_%H%M%S")

echo "Starting backup at $(date)"

# Create backup directory for this date
mkdir -p "$BACKUP_DIR/$DATE"

# Backup application files
cp -r /app/weblog/*.jar "$BACKUP_DIR/$DATE/" 2>/dev/null || true
cp -r /app/weblog/Dockerfile "$BACKUP_DIR/$DATE/" 2>/dev/null || true

# Backup Nginx configuration
cp -r /etc/nginx/sites-available/weblog "$BACKUP_DIR/$DATE/nginx-weblog.conf"

# Backup frontend files
tar -czf "$BACKUP_DIR/$DATE/frontend-files.tar.gz" -C /docker/nginx/html . 2>/dev/null || true

# Keep only last 7 days of backups
find "$BACKUP_DIR" -type d -name "20*" -mtime +7 -exec rm -rf {} \; 2>/dev/null || true

echo "Backup completed at $(date)"
echo "Backup saved to: $BACKUP_DIR/$DATE"
EOF

sudo chmod +x /usr/local/bin/weblog-backup.sh
```

### Step 2: Setup Automated Backups

```bash
# Add to crontab for daily backups at 2 AM
(crontab -l 2>/dev/null; echo "0 2 * * * /usr/local/bin/weblog-backup.sh >> /var/log/weblog-backup.log 2>&1") | crontab -

# Verify crontab
crontab -l
```

### Step 3: System Maintenance Script

```bash
sudo tee /usr/local/bin/weblog-maintenance.sh > /dev/null <<'EOF'
#!/bin/bash

echo "Starting system maintenance at $(date)"

# Update system packages
sudo apt update && sudo apt upgrade -y

# Clean package cache
sudo apt autoremove -y
sudo apt autoclean

# Clean Docker
docker system prune -f

# Rotate logs
sudo logrotate -f /etc/logrotate.conf

# Restart services if needed
sudo systemctl reload nginx

echo "Maintenance completed at $(date)"
EOF

sudo chmod +x /usr/local/bin/weblog-maintenance.sh

# Schedule monthly maintenance
(crontab -l 2>/dev/null; echo "0 3 1 * * /usr/local/bin/weblog-maintenance.sh >> /var/log/weblog-maintenance.log 2>&1") | crontab -
```

---

## 🚨 Troubleshooting

### Common Issues and Solutions

#### 1. **Cannot Connect to Instance**

```bash
# Check security group rules
# Ensure SSH (22) is open to your IP
# Verify key file permissions
chmod 400 your-key.pem

# Test connection
ssh -v -i your-key.pem ubuntu@your-ec2-ip
```

#### 2. **Nginx Won't Start**

```bash
# Check configuration
sudo nginx -t

# Check port conflicts
sudo netstat -tulpn | grep :80

# Check logs
sudo tail -f /var/log/nginx/error.log

# Restart nginx
sudo systemctl restart nginx
```

#### 3. **Docker Issues**

```bash
# Check Docker status
sudo systemctl status docker

# Check user permissions
groups $USER
# Should include 'docker'

# Restart Docker
sudo systemctl restart docker

# Check logs
sudo journalctl -u docker.service
```

#### 4. **Application Won't Start**

```bash
# Check container status
docker ps -a

# Check container logs
docker logs weblog-web

# Check port availability
sudo netstat -tulpn | grep :8080

# Check firewall
sudo ufw status
```

#### 5. **High Memory Usage**

```bash
# Check memory usage
free -h
htop

# Check swap
swapon --show

# Add more swap if needed
sudo fallocate -l 2G /swapfile2
sudo chmod 600 /swapfile2
sudo mkswap /swapfile2
sudo swapon /swapfile2
```

### Useful Commands

```bash
# System monitoring
weblog-status          # Custom status script
htop                   # Process monitor
df -h                  # Disk usage
free -h                # Memory usage
sudo netstat -tulpn    # Network connections

# Service management
sudo systemctl status nginx
sudo systemctl status docker
docker ps              # Running containers
docker logs weblog-web # Application logs

# Log files
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
sudo tail -f /var/log/syslog
docker logs -f weblog-web

# Backup and maintenance
weblog-backup.sh       # Manual backup
weblog-maintenance.sh  # Manual maintenance
sudo logrotate -f /etc/logrotate.conf  # Force log rotation
```

---

## ✅ Verification Checklist

After completing the setup, verify everything is working:

```bash
# 1. System health
sudo systemctl status nginx
sudo systemctl status docker
free -h && df -h

# 2. Network connectivity
curl http://localhost          # Should show nginx default page
curl http://your-ec2-ip        # Should be accessible from internet

# 3. Security
sudo ufw status               # Should be active
sudo fail2ban-client status   # Should show jails

# 4. Directories
ls -la /app/weblog/
ls -la /docker/nginx/html/

# 5. Permissions
groups ubuntu                 # Should include docker, www-data

# 6. Logs
sudo tail /var/log/nginx/access.log
sudo tail /var/log/auth.log
```

---

## 🎉 Server Setup Complete!

Your AWS server is now fully configured and ready for Jenkins deployment!

### **What's Ready:**
✅ Ubuntu 22.04 LTS on AWS EC2
✅ Docker installed and configured
✅ Nginx with optimized configuration
✅ Security hardening (UFW, Fail2Ban, SSH)
✅ Directory structure for applications
✅ Monitoring and maintenance scripts
✅ Automated backups
✅ Log rotation and management

### **Next Steps:**
1. **Test server accessibility**: `http://your-ec2-ip`
2. **Configure Jenkins**: Use the main deployment guide
3. **Deploy your application**: Run the CI/CD pipeline
4. **Monitor performance**: Use the provided monitoring tools

Your server is production-ready! 🚀