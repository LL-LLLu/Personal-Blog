#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}   Weblog Docker Local Setup - Quick Start ${NC}"
echo -e "${GREEN}============================================${NC}"
echo ""

# Function to print colored messages
print_status() {
    echo -e "${YELLOW}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if Docker is installed and running
print_status "Checking Docker installation..."
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed!"
    echo "Please install Docker Desktop from: https://www.docker.com/products/docker-desktop/"
    exit 1
fi

if ! docker info &> /dev/null; then
    print_error "Docker is not running!"
    echo "Please start Docker Desktop and try again."
    exit 1
fi
print_success "Docker is installed and running"

# Check Docker Compose
if ! command -v docker compose &> /dev/null; then
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed!"
        exit 1
    fi
    # Use docker-compose if docker compose doesn't work
    alias docker compose='docker-compose'
fi
print_success "Docker Compose is available"

# Navigate to docker directory
cd "$(dirname "$0")"
DOCKER_DIR=$(pwd)
PROJECT_ROOT=$(dirname "$DOCKER_DIR")

print_status "Project root: $PROJECT_ROOT"

# Check if required directories exist
print_status "Checking project structure..."
if [ ! -d "$PROJECT_ROOT/weblog/weblog-springboot" ]; then
    print_error "Spring Boot project not found at $PROJECT_ROOT/weblog/weblog-springboot"
    echo "Please ensure your project structure is correct."
    exit 1
fi

if [ ! -d "$PROJECT_ROOT/blog-vue3" ]; then
    print_error "Vue.js project not found at $PROJECT_ROOT/blog-vue3"
    echo "Please ensure your project structure is correct."
    exit 1
fi
print_success "Project structure verified"

# Create necessary directories
print_status "Creating necessary directories..."
mkdir -p "$DOCKER_DIR/data/mysql"
mkdir -p "$DOCKER_DIR/data/minio"
mkdir -p "$DOCKER_DIR/logs"
mkdir -p "$DOCKER_DIR/nginx/ssl"
print_success "Directories created"

# Check if ports are available
print_status "Checking port availability..."
PORTS=(3306 8080 80 9000 9001)
PORT_CONFLICT=false

for port in "${PORTS[@]}"; do
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_error "Port $port is already in use!"
        PORT_CONFLICT=true
    fi
done

if [ "$PORT_CONFLICT" = true ]; then
    echo ""
    echo "Please stop the services using these ports or modify docker-compose.yml"
    echo "To find what's using a port: lsof -i :PORT_NUMBER"
    exit 1
fi
print_success "All required ports are available"

# Build Spring Boot application
print_status "Building Spring Boot backend..."
cd "$PROJECT_ROOT/weblog/weblog-springboot"

if [ -f "mvnw" ]; then
    ./mvnw clean package -DskipTests -q
elif command -v mvn &> /dev/null; then
    mvn clean package -DskipTests -q
else
    print_error "Maven is not installed and mvnw wrapper not found!"
    echo "Please install Maven: brew install maven (macOS) or apt-get install maven (Linux)"
    exit 1
fi

if [ ! -f "weblog-web/target/weblog-web-1.0.0-SNAPSHOT.jar" ]; then
    print_error "Failed to build Spring Boot application!"
    echo "Check the build logs above for errors."
    exit 1
fi
print_success "Spring Boot backend built successfully"

# Build Vue.js application
print_status "Building Vue.js frontend..."
cd "$PROJECT_ROOT/blog-vue3"

if ! command -v npm &> /dev/null; then
    print_error "npm is not installed!"
    echo "Please install Node.js and npm from: https://nodejs.org/"
    exit 1
fi

print_status "Installing frontend dependencies..."
npm install --silent

print_status "Building frontend application..."
npm run build

if [ ! -d "dist" ]; then
    print_error "Failed to build Vue.js application!"
    exit 1
fi
print_success "Vue.js frontend built successfully"

# Return to docker directory
cd "$DOCKER_DIR"

# Stop any existing containers
print_status "Stopping any existing containers..."
docker compose down 2>/dev/null
print_success "Cleaned up existing containers"

# Start services
print_status "Starting Docker containers..."
docker compose up -d

# Wait for services to be healthy
print_status "Waiting for services to be ready..."
MAX_WAIT=60
WAIT_COUNT=0

while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
    MYSQL_READY=$(docker compose exec -T mysql mysqladmin ping -h localhost -u root -p123456 2>/dev/null | grep -c "alive" || true)
    
    if [ "$MYSQL_READY" = "1" ]; then
        print_success "MySQL is ready"
        break
    fi
    
    sleep 2
    WAIT_COUNT=$((WAIT_COUNT + 2))
    echo -n "."
done

if [ $WAIT_COUNT -ge $MAX_WAIT ]; then
    print_error "Services failed to start within timeout"
    echo "Check logs with: docker compose logs"
    exit 1
fi

# Configure MinIO
print_status "Configuring MinIO storage..."
sleep 5  # Give MinIO time to start

# Try to configure MinIO
docker exec weblog-minio sh -c "
    mc alias set local http://localhost:9000 minioadmin minioadmin >/dev/null 2>&1
    mc mb local/weblog --ignore-existing >/dev/null 2>&1
    mc anonymous set public local/weblog >/dev/null 2>&1
" || true

print_success "MinIO configured"

# Final status check
print_status "Verifying all services..."
docker compose ps

# Check if all services are running
BACKEND_STATUS=$(docker compose ps backend --format json | grep -c '"State":"running"' || true)
FRONTEND_STATUS=$(docker compose ps frontend --format json | grep -c '"State":"running"' || true)
MYSQL_STATUS=$(docker compose ps mysql --format json | grep -c '"State":"running"' || true)
MINIO_STATUS=$(docker compose ps minio --format json | grep -c '"State":"running"' || true)

ALL_RUNNING=true
if [ "$BACKEND_STATUS" != "1" ]; then
    print_error "Backend is not running properly"
    ALL_RUNNING=false
fi
if [ "$FRONTEND_STATUS" != "1" ]; then
    print_error "Frontend is not running properly"
    ALL_RUNNING=false
fi
if [ "$MYSQL_STATUS" != "1" ]; then
    print_error "MySQL is not running properly"
    ALL_RUNNING=false
fi
if [ "$MINIO_STATUS" != "1" ]; then
    print_error "MinIO is not running properly"
    ALL_RUNNING=false
fi

if [ "$ALL_RUNNING" = false ]; then
    echo ""
    echo "Some services failed to start. Check logs with:"
    echo "  docker compose logs [service-name]"
    echo ""
    echo "Try restarting with:"
    echo "  docker compose down"
    echo "  docker compose up -d"
    exit 1
fi

# Success message
echo ""
echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}     🎉 Application Started Successfully!   ${NC}"
echo -e "${GREEN}============================================${NC}"
echo ""
echo -e "${GREEN}Access Points:${NC}"
echo "  📱 Frontend:      http://localhost"
echo "  🔧 Backend API:   http://localhost:8080"
echo "  📦 MinIO Console: http://localhost:9001"
echo "  🗄️  MySQL:        localhost:3306"
echo ""
echo -e "${GREEN}Default Credentials:${NC}"
echo "  Admin Panel:  admin / 123456"
echo "  MinIO:        minioadmin / minioadmin"
echo "  MySQL:        root / 123456"
echo ""
echo -e "${GREEN}Useful Commands:${NC}"
echo "  View logs:      docker compose logs -f"
echo "  Stop services:  docker compose down"
echo "  Restart:        docker compose restart [service]"
echo "  Check status:   docker compose ps"
echo ""
echo -e "${YELLOW}Note: First startup may take a few minutes for all services to be fully ready.${NC}"
echo -e "${YELLOW}If the frontend doesn't load immediately, wait 30 seconds and refresh.${NC}"