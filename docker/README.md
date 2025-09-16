# Weblog Docker Setup

This Docker setup provides a complete containerized environment for the Weblog application including:

- **MySQL 5.7** - Database (aligned with production)
- **MinIO** - Object storage for files/images
- **Spring Boot Backend** - API server
- **Vue.js Frontend** - Web application
- **Nginx** - Reverse proxy and SSL termination

## Quick Start

1. **Prerequisites**
   ```bash
   # Install Docker and Docker Compose
   docker --version
   docker-compose --version
   ```

2. **Start the application**
   ```bash
   cd docker
   chmod +x start.sh
   ./start.sh
   ```

3. **Access the application**
   - Frontend: https://localhost
   - Backend API: http://localhost:8080
   - MinIO Console: http://localhost:9001
   - MySQL: localhost:3306

## Manual Setup

If you prefer to run commands manually:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild and start
docker-compose up -d --build
```

## Configuration

### Database
- **Host**: localhost:3306
- **Database**: weblog
- **Username**: root
- **Password**: 123456

### MinIO
- **Console**: http://localhost:9001
- **API**: http://localhost:9000
- **Username**: quanxiaoha
- **Password**: quanxiaoha
- **Bucket**: weblog

### Default Admin User
- **Username**: admin
- **Password**: 123456

## File Structure

```
docker/
├── docker-compose.yml         # Full stack Docker Compose
├── docker-compose-infra.yml  # Infrastructure only (MySQL + MinIO)
├── init.sql                  # Database schema and initial data
├── mysql.cnf                 # MySQL configuration
├── application-docker.yml    # Spring Boot configuration for Docker
├── nginx/
│   └── frontend.conf        # Nginx configuration
├── start.sh                 # Setup and start script
├── run-backend-local.sh     # Run backend locally with Docker services
├── DEPLOYMENT_COMPARISON.md # Comparison with Alibaba Cloud setup
└── README.md               # This file
```

## Services

### MySQL Database
- Initializes with complete schema
- Creates default admin user
- Persistent data storage
- Custom MySQL configuration

### MinIO Object Storage
- File and image storage
- CORS configured for web access
- Web-based admin console
- S3-compatible API

### Spring Boot Backend
- Multi-stage Docker build
- Health checks enabled
- Environment-based configuration
- Log file persistence

### Vue.js Frontend
- Multi-stage build with Nginx
- Production-optimized
- API proxy configuration
- Static asset caching

### Nginx Reverse Proxy
- SSL termination
- Rate limiting
- Security headers
- Health checks

## Deployment Options

We provide multiple deployment options to suit different needs:

### Option 1: Full Docker Stack (Default)
Everything runs in Docker containers.
```bash
docker-compose up -d
```

### Option 2: Hybrid Development (Recommended)
Infrastructure in Docker, Spring Boot runs locally for easier debugging.
```bash
# Start infrastructure only
docker-compose -f docker-compose-infra.yml up -d

# Run Spring Boot locally
cd ../weblog/weblog-springboot
mvn clean package -DskipTests
java -jar weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Option 3: IntelliJ IDEA Development
```bash
./run-backend-local.sh
# Then run WeblogWebApplication in IntelliJ IDEA
```

For detailed comparison with Alibaba Cloud deployment, see [DEPLOYMENT_COMPARISON.md](DEPLOYMENT_COMPARISON.md).

## Development

### Rebuilding Services

```bash
# Rebuild specific service
docker-compose build backend
docker-compose build frontend

# Rebuild and restart
docker-compose up -d --build backend
```

### Logs and Debugging

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f mysql

# Execute commands in containers
docker-compose exec mysql mysql -u root -p weblog
docker-compose exec backend bash
```

### Database Operations

```bash
# Connect to MySQL
docker-compose exec mysql mysql -u root -p123456 weblog

# Backup database
docker-compose exec mysql mysqldump -u root -p123456 weblog > backup.sql

# Restore database
docker-compose exec -T mysql mysql -u root -p123456 weblog < backup.sql
```

### MinIO Operations

```bash
# MinIO client operations
docker-compose exec minio mc ls myminio/weblog
docker-compose exec minio mc cp /data/file.jpg myminio/weblog/
```

## Production Considerations

1. **SSL Certificates**: Replace self-signed certificates with proper SSL certificates
2. **Environment Variables**: Use Docker secrets or external configuration
3. **Database**: Consider using managed database services
4. **Monitoring**: Add monitoring and alerting
5. **Backups**: Implement automated backup strategies
6. **Security**: Review and harden security configurations

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 80, 443, 3306, 8080, 9000, 9001 are available
2. **Permission issues**: Check file permissions and Docker daemon access
3. **Memory issues**: Ensure sufficient Docker memory allocation
4. **Network issues**: Verify Docker network configuration

### Useful Commands

```bash
# Check container status
docker-compose ps

# Check resource usage
docker stats

# Clean up
docker-compose down -v  # Remove volumes
docker system prune     # Clean up unused resources
```

## Support

For issues and questions:
1. Check the logs: `docker-compose logs -f`
2. Verify all containers are running: `docker-compose ps`
3. Check network connectivity between containers
4. Review configuration files for any typos or misconfigurations