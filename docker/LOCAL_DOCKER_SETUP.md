# Complete Local Docker Setup Guide for Weblog Application

## Prerequisites Installation

### Step 1: Install Docker Desktop

#### For macOS:
1. Download Docker Desktop from: https://www.docker.com/products/docker-desktop/
2. Open the downloaded `.dmg` file
3. Drag Docker to Applications folder
4. Launch Docker from Applications
5. Accept the agreement and provide your password when prompted
6. Wait for Docker to start (whale icon appears in menu bar)

#### For Windows:
1. Download Docker Desktop from: https://www.docker.com/products/docker-desktop/
2. Run the installer
3. Enable WSL 2 when prompted
4. Restart your computer
5. Launch Docker Desktop

#### For Linux (Ubuntu/Debian):
```bash
# Update package index
sudo apt-get update

# Install prerequisites
sudo apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# Add Docker's official GPG key
sudo mkdir -m 0755 -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Set up repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Add your user to docker group
sudo usermod -aG docker $USER

# Log out and back in for group changes to take effect
```

### Step 2: Verify Docker Installation

Open a terminal/command prompt and run:
```bash
# Check Docker version
docker --version
# Expected output: Docker version 24.x.x, build xxxxxxx

# Check Docker Compose version
docker compose version
# Expected output: Docker Compose version v2.x.x

# Test Docker installation
docker run hello-world
# Should download and run a test container
```

## Project Setup

### Step 3: Prepare Project Structure

```bash
# Navigate to your project directory
cd /Users/qilu/Code/IDEA_Projects

# Create the complete structure needed
mkdir -p docker/nginx/ssl
mkdir -p docker/logs
mkdir -p docker/data/mysql
mkdir -p docker/data/minio

# Verify structure
tree docker -L 2
# Should show:
# docker/
# ├── nginx/
# │   └── ssl/
# ├── logs/
# └── data/
#     ├── mysql/
#     └── minio/
```

### Step 4: Create All Required Configuration Files

We need to ensure all configuration files are in place. I'll create a complete setup script:

```bash
cd /Users/qilu/Code/IDEA_Projects/docker
```

#### 4.1 Create the Main Docker Compose File
Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  # MySQL Database
  mysql:
    image: mysql:8.0
    container_name: weblog-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: weblog
      MYSQL_USER: weblog
      MYSQL_PASSWORD: 123456
      MYSQL_ALLOW_EMPTY_PASSWORD: "no"
    ports:
      - "3306:3306"
    volumes:
      - ./data/mysql:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/01-init.sql:ro
      - ./mysql.cnf:/etc/mysql/conf.d/custom.cnf:ro
    command: 
      - --default-authentication-plugin=mysql_native_password
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_general_ci
    networks:
      - weblog-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p123456"]
      interval: 10s
      timeout: 5s
      retries: 5

  # MinIO Object Storage
  minio:
    image: minio/minio:latest
    container_name: weblog-minio
    restart: always
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    ports:
      - "9000:9000"   # API port
      - "9001:9001"   # Console port
    volumes:
      - ./data/minio:/data
    command: server /data --console-address ":9001"
    networks:
      - weblog-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Spring Boot Backend
  backend:
    image: openjdk:11-jre-slim
    container_name: weblog-backend
    restart: always
    depends_on:
      mysql:
        condition: service_healthy
      minio:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/weblog?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: 123456
      MINIO_ENDPOINT: http://minio:9000
      MINIO_ACCESS_KEY: minioadmin
      MINIO_SECRET_KEY: minioadmin
      MINIO_BUCKET_NAME: weblog
    ports:
      - "8080:8080"
    volumes:
      - ../weblog/weblog-springboot/weblog-web/target:/app
      - ./logs:/app/logs
    working_dir: /app
    command: ["java", "-jar", "weblog-web-1.0.0-SNAPSHOT.jar"]
    networks:
      - weblog-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5

  # Vue.js Frontend (served by Nginx)
  frontend:
    image: nginx:alpine
    container_name: weblog-frontend
    restart: always
    depends_on:
      - backend
    ports:
      - "80:80"
      - "5173:80"  # Alternative port for development
    volumes:
      - ../blog-vue3/dist:/usr/share/nginx/html:ro
      - ./nginx/frontend.conf:/etc/nginx/conf.d/default.conf:ro
    networks:
      - weblog-network
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/"]
      interval: 30s
      timeout: 10s
      retries: 3

networks:
  weblog-network:
    driver: bridge

volumes:
  mysql_data:
    driver: local
  minio_data:
    driver: local
```

#### 4.2 Create Frontend Nginx Configuration
Create `nginx/frontend.conf`:
```nginx
server {
    listen 80;
    server_name localhost;
    
    root /usr/share/nginx/html;
    index index.html;
    
    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/json application/xml+rss;

    # Vue.js routing - serve index.html for all routes
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy to backend
    location /api/ {
        proxy_pass http://backend:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # Static file caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### Step 5: Build the Applications

#### 5.1 Build Spring Boot Backend
```bash
cd /Users/qilu/Code/IDEA_Projects/weblog/weblog-springboot

# Clean and build with Maven
./mvnw clean package -DskipTests

# Or if mvnw is not available
mvn clean package -DskipTests

# Verify JAR file was created
ls -la weblog-web/target/*.jar
# Should see: weblog-web-1.0.0-SNAPSHOT.jar
```

#### 5.2 Build Vue.js Frontend
```bash
cd /Users/qilu/Code/IDEA_Projects/blog-vue3

# Install dependencies
npm install

# Build for production
npm run build

# Verify build output
ls -la dist/
# Should see index.html and assets folder
```

### Step 6: Initialize Database and MinIO

#### 6.1 Start Only MySQL First
```bash
cd /Users/qilu/Code/IDEA_Projects/docker

# Start MySQL container
docker compose up -d mysql

# Wait for MySQL to be ready (about 30 seconds)
docker compose logs mysql

# You should see: "ready for connections"
```

#### 6.2 Verify Database Creation
```bash
# Connect to MySQL
docker exec -it weblog-mysql mysql -u root -p123456

# Inside MySQL prompt:
SHOW DATABASES;
USE weblog;
SHOW TABLES;
exit;
```

#### 6.3 Start MinIO and Configure It
```bash
# Start MinIO
docker compose up -d minio

# Wait for MinIO to start
sleep 10

# Create bucket using MinIO client
docker exec weblog-minio mkdir -p /data/weblog

# Or use MinIO Console
# Open browser: http://localhost:9001
# Login: minioadmin / minioadmin
# Create bucket named "weblog"
# Set bucket policy to "public" for read access
```

### Step 7: Start All Services

#### 7.1 Start Everything
```bash
cd /Users/qilu/Code/IDEA_Projects/docker

# Stop any running containers
docker compose down

# Start all services
docker compose up -d

# Watch logs in real-time
docker compose logs -f

# Or view specific service logs
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql
```

#### 7.2 Verify All Services Are Running
```bash
# Check container status
docker compose ps

# Should show:
# NAME              STATUS          PORTS
# weblog-mysql      Up (healthy)    0.0.0.0:3306->3306/tcp
# weblog-minio      Up (healthy)    0.0.0.0:9000->9000/tcp, 0.0.0.0:9001->9001/tcp
# weblog-backend    Up (healthy)    0.0.0.0:8080->8080/tcp
# weblog-frontend   Up (healthy)    0.0.0.0:80->80/tcp
```

### Step 8: Configure MinIO CORS (Important!)

This fixes the image display issue:

```bash
# Method 1: Using MinIO Client in Docker
docker exec -it weblog-minio sh

# Inside container:
mc alias set local http://localhost:9000 minioadmin minioadmin
mc anonymous set public local/weblog
mc admin config set local api cors_allow_origin="*"
mc admin service restart local
exit

# Method 2: Using MinIO Console
# 1. Open http://localhost:9001
# 2. Login: minioadmin / minioadmin
# 3. Go to Buckets → weblog → Access Policy
# 4. Set to "Public"
# 5. Go to Settings → Region & CORS
# 6. Add CORS rule:
#    - Origins: *
#    - Methods: GET, HEAD, POST, PUT, DELETE
#    - Headers: *
```

### Step 9: Access Your Application

#### 9.1 Test Each Component
```bash
# Test MySQL
mysql -h 127.0.0.1 -P 3306 -u root -p123456 weblog -e "SELECT 1"

# Test MinIO
curl http://localhost:9000/minio/health/live

# Test Backend
curl http://localhost:8080/actuator/health

# Test Frontend
curl http://localhost
```

#### 9.2 Open in Browser
- **Frontend Application**: http://localhost or http://localhost:5173
- **Backend API**: http://localhost:8080
- **MinIO Console**: http://localhost:9001 (login: minioadmin/minioadmin)

#### 9.3 Default Login Credentials
- **Admin Panel**: http://localhost/admin
  - Username: `admin`
  - Password: `123456`

## Troubleshooting Guide

### Issue 1: Port Already in Use
```bash
# Check what's using the port
lsof -i :3306  # For MySQL
lsof -i :8080  # For Backend
lsof -i :80    # For Frontend

# Solution: Stop the conflicting service or change ports in docker-compose.yml
```

### Issue 2: Cannot Connect to MySQL
```bash
# Check MySQL logs
docker compose logs mysql

# Common fixes:
# 1. Wait longer for MySQL to initialize
# 2. Check password in docker-compose.yml matches init.sql
# 3. Remove old data: rm -rf docker/data/mysql/*
```

### Issue 3: Backend Cannot Start
```bash
# Check if JAR file exists
ls -la ../weblog/weblog-springboot/weblog-web/target/*.jar

# Rebuild if needed
cd ../weblog/weblog-springboot
mvn clean package -DskipTests

# Check backend logs
docker compose logs backend
```

### Issue 4: Frontend Shows Blank Page
```bash
# Check if dist folder exists
ls -la ../blog-vue3/dist/

# Rebuild frontend if needed
cd ../blog-vue3
npm install
npm run build

# Check nginx logs
docker compose logs frontend
```

### Issue 5: Images Not Displaying
```bash
# This is usually a CORS issue. Fix:
docker exec weblog-minio mc alias set local http://localhost:9000 minioadmin minioadmin
docker exec weblog-minio mc anonymous set public local/weblog
```

## Useful Docker Commands

### Container Management
```bash
# Start all services
docker compose up -d

# Stop all services
docker compose down

# Restart a specific service
docker compose restart backend

# View logs
docker compose logs -f

# View specific service logs
docker compose logs -f backend

# Execute command in container
docker compose exec mysql bash
docker compose exec backend sh
```

### Cleanup Commands
```bash
# Stop and remove containers
docker compose down

# Remove everything including volumes (CAUTION: Deletes all data!)
docker compose down -v

# Clean up unused Docker resources
docker system prune -a

# Remove specific volume
docker volume rm docker_mysql_data
```

### Database Operations
```bash
# Backup database
docker exec weblog-mysql mysqldump -u root -p123456 weblog > backup.sql

# Restore database
docker exec -i weblog-mysql mysql -u root -p123456 weblog < backup.sql

# Connect to MySQL
docker exec -it weblog-mysql mysql -u root -p123456 weblog
```

## Quick Start Script

Create `start-local.sh`:
```bash
#!/bin/bash

echo "🚀 Starting Weblog Application..."

# Function to check if port is available
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        echo "❌ Port $1 is already in use!"
        echo "Please stop the service using port $1 or change the port in docker-compose.yml"
        exit 1
    fi
}

# Check required ports
echo "📍 Checking ports..."
check_port 3306
check_port 8080
check_port 80
check_port 9000
check_port 9001

# Build applications
echo "🔨 Building applications..."

# Build backend
echo "  Building Spring Boot backend..."
cd ../weblog/weblog-springboot
mvn clean package -DskipTests -q

# Build frontend
echo "  Building Vue.js frontend..."
cd ../../blog-vue3
npm install --silent
npm run build --silent

# Start Docker services
cd ../docker
echo "🐳 Starting Docker containers..."
docker compose down
docker compose up -d

# Wait for services
echo "⏳ Waiting for services to start..."
sleep 30

# Configure MinIO
echo "🗄️ Configuring MinIO..."
docker exec weblog-minio mc alias set local http://localhost:9000 minioadmin minioadmin 2>/dev/null
docker exec weblog-minio mc mb local/weblog --ignore-existing 2>/dev/null
docker exec weblog-minio mc anonymous set public local/weblog 2>/dev/null

# Check status
echo "✅ Checking service status..."
docker compose ps

echo ""
echo "🎉 Application is ready!"
echo "📍 Access points:"
echo "  Frontend: http://localhost"
echo "  Backend API: http://localhost:8080"
echo "  MinIO Console: http://localhost:9001"
echo ""
echo "📝 Default credentials:"
echo "  Admin: admin / 123456"
echo "  MinIO: minioadmin / minioadmin"
echo ""
echo "📊 View logs: docker compose logs -f"
echo "🛑 Stop services: docker compose down"
```

Make it executable:
```bash
chmod +x start-local.sh
./start-local.sh
```

## Verification Checklist

- [ ] Docker Desktop is running
- [ ] All containers show as "Up" in `docker compose ps`
- [ ] Frontend loads at http://localhost
- [ ] Backend API responds at http://localhost:8080
- [ ] MinIO Console accessible at http://localhost:9001
- [ ] Can login to admin panel
- [ ] Images upload and display correctly
- [ ] Database operations work (create/read articles)

## Next Steps

1. **Development Workflow**
   - Make code changes
   - Rebuild affected service
   - Restart container: `docker compose restart backend`

2. **Add Sample Data**
   - Login to admin panel
   - Create categories and tags
   - Write sample articles
   - Upload test images

3. **Customize Configuration**
   - Update blog settings in admin panel
   - Modify environment variables in docker-compose.yml
   - Adjust resource limits if needed

4. **Setup IDE Integration**
   - Configure IDE to connect to Docker MySQL
   - Setup remote debugging for Spring Boot
   - Configure hot reload for Vue.js development