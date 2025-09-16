#!/bin/bash
# Script to run Spring Boot backend locally (outside Docker) while using Docker services
# This approach matches the Alibaba Cloud deployment guide

echo "======================================="
echo "Starting Backend Locally with Docker Services"
echo "======================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check command success
check_status() {
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ $1${NC}"
    else
        echo -e "${RED}✗ $1${NC}"
        exit 1
    fi
}

# 1. Start only the infrastructure services in Docker
echo -e "\n${YELLOW}Starting Docker infrastructure services...${NC}"
docker-compose up -d mysql minio
check_status "Docker services started"

# 2. Wait for MySQL to be ready
echo -e "\n${YELLOW}Waiting for MySQL to be ready...${NC}"
for i in {1..30}; do
    if docker exec weblog-mysql mysqladmin ping -h localhost -u root -p123456 &>/dev/null; then
        echo -e "${GREEN}MySQL is ready!${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

# 3. Initialize MinIO bucket if needed
echo -e "\n${YELLOW}Configuring MinIO...${NC}"
docker exec weblog-minio mc alias set myminio http://localhost:9000 minioadmin minioadmin 2>/dev/null || true
docker exec weblog-minio mc mb myminio/weblog 2>/dev/null || true
docker exec weblog-minio mc anonymous set download myminio/weblog 2>/dev/null || true
echo -e "${GREEN}MinIO configured${NC}"

# 4. Build the Spring Boot application
echo -e "\n${YELLOW}Building Spring Boot application...${NC}"
cd ../weblog/weblog-springboot
mvn clean package -DskipTests
check_status "Spring Boot build completed"

# 5. Run Spring Boot locally
echo -e "\n${YELLOW}Starting Spring Boot application locally...${NC}"
echo "You can now run the Spring Boot application from IntelliJ IDEA or with:"
echo ""
echo "cd ../weblog/weblog-springboot/weblog-web"
echo "java -jar target/weblog-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev"
echo ""
echo "Or with Docker network connectivity:"
echo "java -jar target/weblog-web-0.0.1-SNAPSHOT.jar \\"
echo "  --spring.datasource.url=jdbc:mysql://localhost:3306/weblog?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&allowPublicKeyRetrieval=true \\"
echo "  --spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \\"
echo "  --minio.endpoint=http://localhost:9000 \\"
echo "  --minio.accessKey=minioadmin \\"
echo "  --minio.secretKey=minioadmin"

echo -e "\n${GREEN}Infrastructure services are running!${NC}"
echo "MySQL: localhost:3306"
echo "MinIO: localhost:9000 (Console: localhost:9001)"
echo ""
echo "Login credentials:"
echo "MySQL: root/123456"
echo "MinIO: minioadmin/minioadmin"
echo "Blog Admin: admin/123456"