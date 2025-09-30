#!/bin/bash

# Production Deployment Script for Weblog Application
# Usage: ./deploy.sh [environment] [version]

set -euo pipefail

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DOCKER_DIR="${PROJECT_ROOT}/docker"
ENVIRONMENT=${1:-prod}
VERSION=${2:-$(date +%Y%m%d-%H%M%S)}
BACKUP_ENABLED=${BACKUP_ENABLED:-true}
ROLLBACK_ENABLED=${ROLLBACK_ENABLED:-true}

# Colors for output
readonly RED=$'\033[0;31m'
readonly GREEN=$'\033[0;32m'
readonly YELLOW=$'\033[1;33m'
readonly BLUE=$'\033[0;34m'
readonly NC=$'\033[0m' # No Color

# Logging functions
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR:${NC} $1"
    exit 1
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] INFO:${NC} $1"
}

# Check if running as root
check_root() {
    if [[ $EUID -eq 0 ]]; then
        warn "Running as root. Consider using a non-root user for security."
    fi
}

# Validate environment
validate_environment() {
    case "$ENVIRONMENT" in
        dev|staging|prod)
            log "✅ Environment: $ENVIRONMENT"
            ;;
        *)
            error "❌ Invalid environment: $ENVIRONMENT. Must be dev, staging, or prod"
            ;;
    esac
}

# Check prerequisites
check_prerequisites() {
    log "🔍 Checking prerequisites..."

    # Check Docker
    if ! command -v docker &> /dev/null; then
        error "❌ Docker is not installed or not in PATH"
    fi

    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        error "❌ Docker Compose is not installed or not in PATH"
    fi

    # Check Docker daemon
    if ! docker info &> /dev/null; then
        error "❌ Docker daemon is not running"
    fi

    # Check available disk space (minimum 10GB)
    local available_space
    available_space=$(df "$PROJECT_ROOT" | awk 'NR==2 {print $4}')
    if [[ $available_space -lt 10485760 ]]; then # 10GB in KB
        warn "⚠️ Low disk space. Less than 10GB available."
    fi

    # Check available memory (minimum 4GB)
    local available_memory
    if command -v free &> /dev/null; then
        available_memory=$(free -m | awk 'NR==2{printf "%.0f", $7}')
        if [[ $available_memory -lt 4096 ]]; then
            warn "⚠️ Low memory. Less than 4GB available."
        fi
    fi

    log "✅ Prerequisites check passed"
}

# Load environment variables
load_environment() {
    local env_file="${DOCKER_DIR}/.env.${ENVIRONMENT}"

    if [[ -f "$env_file" ]]; then
        log "📄 Loading environment from $env_file"
        # shellcheck disable=SC1090
        set -a && source "$env_file" && set +a
        export VERSION
    else
        warn "⚠️ Environment file not found: $env_file"
        info "Creating default environment file..."
        create_default_env_file "$env_file"
    fi
}

# Create default environment file
create_default_env_file() {
    local env_file="$1"

    cat > "$env_file" <<EOF
# Database Configuration
MYSQL_ROOT_PASSWORD=$(openssl rand -base64 32)
MYSQL_PASSWORD=$(openssl rand -base64 32)

# MinIO Configuration
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=$(openssl rand -base64 32)
MINIO_PUBLIC_URL=http://localhost:9000

# Redis Configuration
REDIS_PASSWORD=$(openssl rand -base64 32)

# Application Version
VERSION=${VERSION}

# Monitoring
GRAFANA_PASSWORD=$(openssl rand -base64 32)

# Ports
MYSQL_PORT=3306
BACKEND_PORT=8080
FRONTEND_HTTP_PORT=80
FRONTEND_HTTPS_PORT=443
MINIO_API_PORT=9000
MINIO_CONSOLE_PORT=9001
REDIS_PORT=6379
PROMETHEUS_PORT=9090
GRAFANA_PORT=3000

# Paths
DATA_PATH=${DOCKER_DIR}/data
EOF

    warn "⚠️ Generated random passwords in $env_file"
    warn "⚠️ Please review and secure these credentials!"
}

# Create required directories
create_directories() {
    log "📁 Creating required directories..."

    local directories=(
        "${DOCKER_DIR}/data/mysql"
        "${DOCKER_DIR}/data/minio"
        "${DOCKER_DIR}/logs/backend"
        "${DOCKER_DIR}/logs/frontend"
        "${DOCKER_DIR}/logs/nginx"
        "${DOCKER_DIR}/logs/mysql"
        "${DOCKER_DIR}/logs/minio"
        "${DOCKER_DIR}/backups"
        "${DOCKER_DIR}/ssl"
        "${DOCKER_DIR}/monitoring"
        "${DOCKER_DIR}/nginx"
        "${DOCKER_DIR}/mysql"
        "${DOCKER_DIR}/redis"
    )

    for dir in "${directories[@]}"; do
        mkdir -p "$dir"
    done

    log "✅ Directories created"
}

# Backup current deployment
backup_current_deployment() {
    if [[ "$BACKUP_ENABLED" != "true" ]]; then
        info "🚫 Backup disabled, skipping..."
        return 0
    fi

    log "💾 Creating backup of current deployment..."

    local backup_dir="${DOCKER_DIR}/backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$backup_dir"

    # Backup database
    if docker ps | grep -q weblog-mysql; then
        log "  Backing up MySQL database..."
        docker exec weblog-mysql-prod mysqldump \
            -u root -p"${MYSQL_ROOT_PASSWORD}" \
            --single-transaction \
            --routines \
            --triggers \
            weblog > "${backup_dir}/database_backup.sql" || warn "Database backup failed"
    fi

    # Backup MinIO data
    if docker ps | grep -q weblog-minio; then
        log "  Backing up MinIO data..."
        docker exec weblog-minio-prod tar -czf - /data > "${backup_dir}/minio_backup.tar.gz" || warn "MinIO backup failed"
    fi

    # Backup configuration files
    log "  Backing up configuration files..."
    cp -r "${DOCKER_DIR}"/*.yml "${backup_dir}/" 2>/dev/null || true
    cp "${DOCKER_DIR}/.env.${ENVIRONMENT}" "${backup_dir}/" 2>/dev/null || true

    log "✅ Backup completed: $backup_dir"
    export BACKUP_DIR="$backup_dir"
}

# Build applications
build_applications() {
    log "🔨 Building applications..."

    # Build Spring Boot backend
    log "  Building Spring Boot backend..."
    cd "${PROJECT_ROOT}/weblog/weblog-springboot"

    if command -v mvn &> /dev/null; then
        mvn clean package -DskipTests -q || error "❌ Backend build failed"
    else
        error "❌ Maven not found. Please install Maven or run build in IDE"
    fi

    # Build Vue.js frontend
    log "  Building Vue.js frontend..."
    cd "${PROJECT_ROOT}/blog-vue3"

    if command -v npm &> /dev/null; then
        npm ci --silent || error "❌ Frontend dependency installation failed"
        npm run build --silent || error "❌ Frontend build failed"
    else
        error "❌ npm not found. Please install Node.js and npm"
    fi

    log "✅ Applications built successfully"
}

# Pre-deployment health check
pre_deployment_check() {
    log "🏥 Pre-deployment health check..."

    # Check if ports are available
    local ports=(
        "${MYSQL_PORT:-3306}"
        "${BACKEND_PORT:-8080}"
        "${FRONTEND_HTTP_PORT:-80}"
        "${MINIO_API_PORT:-9000}"
        "${MINIO_CONSOLE_PORT:-9001}"
    )

    for port in "${ports[@]}"; do
        if lsof -Pi ":$port" -sTCP:LISTEN -t >/dev/null; then
            warn "⚠️ Port $port is already in use"
        fi
    done

    # Check if required files exist
    local required_files=(
        "${PROJECT_ROOT}/weblog/weblog-springboot/weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar"
        "${PROJECT_ROOT}/blog-vue3/dist/index.html"
    )

    for file in "${required_files[@]}"; do
        if [[ ! -f "$file" ]]; then
            error "❌ Required file not found: $file"
        fi
    done

    log "✅ Pre-deployment check passed"
}

# Deploy services
deploy_services() {
    log "🚀 Deploying services..."

    cd "$DOCKER_DIR"

    # Set environment variables
    export VERSION
    export COMPOSE_PROJECT_NAME="weblog-${ENVIRONMENT}"

    # Choose the correct compose file
    local compose_file="docker-compose.yml"
    if [[ -f "docker-compose.${ENVIRONMENT}.yml" ]]; then
        compose_file="docker-compose.${ENVIRONMENT}.yml"
    fi

    log "  Using compose file: $compose_file"

    # Stop existing services gracefully
    log "  Stopping existing services..."
    docker-compose -f "$compose_file" down --timeout 30 || warn "Failed to stop some services"

    # Pull latest base images
    log "  Pulling latest images..."
    docker-compose -f "$compose_file" pull || warn "Failed to pull some images"

    # Build custom images
    log "  Building custom images..."
    docker-compose -f "$compose_file" build --no-cache || error "❌ Image build failed"

    # Start services
    log "  Starting services..."
    docker-compose -f "$compose_file" up -d || error "❌ Service startup failed"

    log "✅ Services deployed successfully"
}

# Wait for services to be ready
wait_for_services() {
    log "⏳ Waiting for services to be ready..."

    local max_wait=300 # 5 minutes
    local wait_interval=10
    local elapsed=0

    while [[ $elapsed -lt $max_wait ]]; do
        log "  Checking service health... (${elapsed}s/${max_wait}s)"

        # Check if all containers are running
        local containers_up
        containers_up=$(docker-compose ps | grep -c "Up" || echo "0")

        if [[ $containers_up -ge 3 ]]; then # At least MySQL, backend, frontend
            log "✅ All services are running"
            break
        fi

        sleep $wait_interval
        elapsed=$((elapsed + wait_interval))
    done

    if [[ $elapsed -ge $max_wait ]]; then
        error "❌ Services did not start within ${max_wait} seconds"
    fi
}

# Health check deployment
verify_deployment() {
    log "🏥 Verifying deployment health..."

    local max_retries=30
    local retry_delay=10
    local services=(
        "http://localhost:${BACKEND_PORT:-8080}/actuator/health:Backend"
        "http://localhost:${FRONTEND_HTTP_PORT:-80}/health.json:Frontend"
        "http://localhost:${MINIO_API_PORT:-9000}/minio/health/live:MinIO"
    )

    for service_info in "${services[@]}"; do
        local url="${service_info%:*}"
        local name="${service_info#*:}"

        log "  Checking $name health..."

        for i in $(seq 1 $max_retries); do
            if curl -sf "$url" >/dev/null 2>&1; then
                log "  ✅ $name is healthy"
                break
            fi

            if [[ $i -eq $max_retries ]]; then
                error "❌ $name health check failed after $max_retries attempts"
            fi

            log "    Waiting for $name... (attempt $i/$max_retries)"
            sleep $retry_delay
        done
    done

    log "✅ All services are healthy"
}

# Setup MinIO bucket and policies
setup_minio() {
    log "🗄️ Setting up MinIO..."

    # Wait for MinIO to be fully ready
    sleep 30

    # Create bucket and set policies
    docker exec weblog-minio-prod sh -c '
        mc alias set local http://localhost:9000 $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD
        mc mb local/weblog --ignore-existing
        mc anonymous set public local/weblog
        mc admin config set local api cors_allow_origin="*"
        mc admin service restart local
    ' || warn "MinIO setup failed"

    log "✅ MinIO configured"
}

# Post-deployment tasks
post_deployment_tasks() {
    log "📝 Running post-deployment tasks..."

    # Setup MinIO
    setup_minio

    # Display service information
    display_service_info

    # Clean up old Docker resources
    cleanup_docker_resources

    log "✅ Post-deployment tasks completed"
}

# Display service information
display_service_info() {
    log "📊 Service Information:"
    echo
    echo "🌐 Access Points:"
    echo "   Frontend:      http://localhost:${FRONTEND_HTTP_PORT:-80}"
    echo "   Backend API:   http://localhost:${BACKEND_PORT:-8080}"
    echo "   MinIO Console: http://localhost:${MINIO_CONSOLE_PORT:-9001}"
    echo "   Admin Panel:   http://localhost:${FRONTEND_HTTP_PORT:-80}/admin"
    echo
    echo "🔐 Default Credentials:"
    echo "   Admin:    admin / 123456"
    echo "   MinIO:    ${MINIO_ROOT_USER} / [check .env file]"
    echo
    echo "📊 Container Status:"
    docker-compose ps
    echo
    echo "💾 Resource Usage:"
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
    echo
}

# Cleanup Docker resources
cleanup_docker_resources() {
    log "🧹 Cleaning up Docker resources..."

    # Remove dangling images
    docker image prune -f >/dev/null 2>&1 || true

    # Remove unused networks
    docker network prune -f >/dev/null 2>&1 || true

    # Remove build cache (optional - comment out for faster builds)
    # docker builder prune -f >/dev/null 2>&1 || true

    log "✅ Cleanup completed"
}

# Rollback function
rollback() {
    if [[ "$ROLLBACK_ENABLED" != "true" ]]; then
        info "🚫 Rollback disabled"
        return 0
    fi

    if [[ -z "${BACKUP_DIR:-}" ]]; then
        error "❌ No backup directory found for rollback"
    fi

    warn "🔄 Rolling back deployment..."

    cd "$DOCKER_DIR"

    # Stop current services
    docker-compose down || true

    # Restore configuration
    if [[ -f "${BACKUP_DIR}/docker-compose.yml" ]]; then
        cp "${BACKUP_DIR}/docker-compose.yml" ./ || true
    fi

    if [[ -f "${BACKUP_DIR}/.env.${ENVIRONMENT}" ]]; then
        cp "${BACKUP_DIR}/.env.${ENVIRONMENT}" ./ || true
    fi

    # Restore database
    if [[ -f "${BACKUP_DIR}/database_backup.sql" ]]; then
        warn "Manual database restore required: ${BACKUP_DIR}/database_backup.sql"
    fi

    # Start services
    docker-compose up -d || error "❌ Rollback failed"

    warn "✅ Rollback completed"
}

# Signal handlers
trap 'error "Deployment interrupted"' INT TERM
trap 'rollback' ERR

# Main deployment workflow
main() {
    log "🚀 Starting deployment process..."
    log "   Environment: $ENVIRONMENT"
    log "   Version: $VERSION"
    log "   Project: $PROJECT_ROOT"
    echo

    check_root
    validate_environment
    check_prerequisites
    load_environment
    create_directories
    backup_current_deployment
    build_applications
    pre_deployment_check
    deploy_services
    wait_for_services
    verify_deployment
    post_deployment_tasks

    echo
    log "🎉 Deployment completed successfully!"
    log "🕒 Total time: $((SECONDS / 60))m $((SECONDS % 60))s"
}

# Help function
show_help() {
    cat << EOF
Weblog Application Deployment Script

USAGE:
    $0 [ENVIRONMENT] [VERSION]

ARGUMENTS:
    ENVIRONMENT    Deployment environment (dev|staging|prod) [default: prod]
    VERSION        Version tag for images [default: timestamp]

ENVIRONMENT VARIABLES:
    BACKUP_ENABLED      Enable/disable backup [default: true]
    ROLLBACK_ENABLED    Enable/disable rollback on error [default: true]

EXAMPLES:
    $0                           # Deploy to prod with timestamp version
    $0 staging                   # Deploy to staging
    $0 prod v1.2.3              # Deploy specific version to prod

    BACKUP_ENABLED=false $0      # Deploy without backup

FILES:
    .env.{environment}           # Environment-specific configuration
    docker-compose.{env}.yml     # Environment-specific compose file

For more information, see the DOCKER_DEPLOYMENT_GUIDE.md
EOF
}

# Parse command line arguments
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac