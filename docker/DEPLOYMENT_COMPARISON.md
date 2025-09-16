# Deployment Comparison: Local vs Alibaba Cloud

## Overview
This document compares the local Docker setup with the Alibaba Cloud deployment guide and provides multiple deployment options.

## Key Differences Identified

### 1. MinIO Credentials
- **Local (Original)**: minioadmin/minioadmin
- **Alibaba Cloud**: quanxiaoha/quanxiaoha
- **Status**: ✅ Updated to match production

### 2. MySQL Version
- **Local (Original)**: MySQL 8.0
- **Alibaba Cloud**: MySQL 5.7
- **Status**: ✅ Updated to MySQL 5.7 for compatibility

### 3. Spring Boot Deployment
- **Local**: Runs inside Docker container
- **Alibaba Cloud**: Runs on host machine (outside Docker)
- **Status**: ✅ Created hybrid option

### 4. Database Driver
- **Local**: P6Spy driver for SQL logging
- **Alibaba Cloud**: Standard MySQL driver
- **Status**: ✅ Override with JVM parameters when needed

## Deployment Options

### Option 1: Full Docker Stack (Development)
Everything runs in Docker containers.

```bash
# Use the main docker-compose.yml
docker-compose up -d

# Access points:
# - Frontend: http://localhost:5173
# - Backend API: http://localhost:8080
# - MinIO Console: http://localhost:9001
```

**Pros:**
- Fully containerized
- Easy to start/stop
- Consistent environment

**Cons:**
- Harder to debug Spring Boot
- Requires rebuilding Docker image for code changes

### Option 2: Hybrid Approach (Recommended for Development)
Infrastructure in Docker, Spring Boot runs locally (matches Alibaba Cloud guide).

```bash
# Start only infrastructure
docker-compose -f docker-compose-infra.yml up -d

# Build and run Spring Boot locally
cd ../weblog/weblog-springboot
mvn clean package -DskipTests
java -jar weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

**Pros:**
- Easy debugging in IntelliJ IDEA
- Quick code changes without Docker rebuild
- Matches production deployment pattern

**Cons:**
- Requires Java installed locally
- Need to manage Spring Boot process separately

### Option 3: IntelliJ IDEA Development
Best for active development with IDE integration.

```bash
# Start infrastructure
./run-backend-local.sh

# Then in IntelliJ IDEA:
# 1. Open the weblog-springboot project
# 2. Run WeblogWebApplication with profile: dev
# 3. Set JVM options if needed:
#    -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## Configuration Files

### Application Profiles
- `application-dev.yml`: Local development (P6Spy driver)
- `application-docker.yml`: Docker deployment (MySQL driver)
- `application-prod.yml`: Production deployment

### Docker Compose Files
- `docker-compose.yml`: Full stack deployment
- `docker-compose-infra.yml`: Infrastructure only
- `run-backend-local.sh`: Helper script for hybrid deployment

## Database Initialization
The `init.sql` file contains:
- Complete database schema (10 tables)
- Admin user: admin/123456 (bcrypt encrypted)
- Default blog settings

## Network Configuration

### Docker Network
- Internal network: `weblog-network`
- Services communicate using container names
- MySQL: `mysql:3306`
- MinIO: `minio:9000`

### Host Access
When Spring Boot runs on host:
- MySQL: `localhost:3306`
- MinIO: `localhost:9000`

## Troubleshooting

### Jackson Library Issues
If you encounter `StreamConstraintsException`:
1. Check Jackson version compatibility
2. Use full JDK image instead of JRE-slim
3. Update dependencies in pom.xml

### P6Spy Driver Issues
Override with standard MySQL driver:
```bash
java -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver -jar app.jar
```

### Port Conflicts
Check and kill processes:
```bash
lsof -i :9000  # Check what's using port
kill -9 <PID>  # Kill the process
```

### Maven Build Issues
If Docker locks target directory:
1. Stop Docker containers
2. Clean Maven: `mvn clean`
3. Rebuild: `mvn package -DskipTests`

## Security Notes

### Credentials Summary
| Service | Username | Password | Note |
|---------|----------|----------|------|
| MySQL | root | 123456 | Change in production |
| MinIO | quanxiaoha | quanxiaoha | Change in production |
| Blog Admin | admin | 123456 | Change after first login |

### Production Recommendations
1. Use environment variables for credentials
2. Enable SSL/TLS for all services
3. Use strong passwords
4. Implement proper firewall rules
5. Regular backup strategy

## Quick Start Commands

```bash
# Full Docker deployment
docker-compose up -d

# Infrastructure only
docker-compose -f docker-compose-infra.yml up -d

# Stop all services
docker-compose down

# Clean everything (including data)
docker-compose down -v
rm -rf data/

# View logs
docker-compose logs -f backend
docker-compose logs -f mysql
```