#!/bin/bash

# Rollback Script for Weblog Application
# Usage: ./rollback.sh [backup_timestamp]

set -euo pipefail

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DOCKER_DIR="${PROJECT_ROOT}/docker"
BACKUP_TIMESTAMP=${1:-""}

# Colors for output
readonly RED=$'\033[0;31m'
readonly GREEN=$'\033[0;32m'
readonly YELLOW=$'\033[1;33m'
readonly BLUE=$'\033[0;34m'
readonly NC=$'\033[0m'

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

# Find the most recent backup if not specified
find_latest_backup() {
    local backup_dir="${DOCKER_DIR}/backups"

    if [[ ! -d "$backup_dir" ]]; then
        error "❌ Backup directory not found: $backup_dir"
    fi

    if [[ -z "$BACKUP_TIMESTAMP" ]]; then
        BACKUP_TIMESTAMP=$(ls -1 "$backup_dir" | grep -E '^[0-9]{8}_[0-9]{6}$' | sort -r | head -n1)

        if [[ -z "$BACKUP_TIMESTAMP" ]]; then
            error "❌ No backups found in $backup_dir"
        fi

        log "🔍 Using latest backup: $BACKUP_TIMESTAMP"
    fi

    BACKUP_PATH="${backup_dir}/${BACKUP_TIMESTAMP}"

    if [[ ! -d "$BACKUP_PATH" ]]; then
        error "❌ Backup not found: $BACKUP_PATH"
    fi
}

# Validate backup contents
validate_backup() {
    log "🔍 Validating backup..."

    local required_files=(
        "database_backup.sql"
    )

    for file in "${required_files[@]}"; do
        if [[ ! -f "${BACKUP_PATH}/$file" ]]; then
            warn "⚠️ Backup file missing: $file"
        else
            log "  ✅ Found: $file"
        fi
    done

    log "✅ Backup validation completed"
}

# Create pre-rollback backup
create_pre_rollback_backup() {
    log "💾 Creating pre-rollback backup..."

    local pre_rollback_dir="${DOCKER_DIR}/backups/pre_rollback_$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$pre_rollback_dir"

    # Backup current database
    if docker ps --format '{{.Names}}' | grep -q weblog-mysql; then
        log "  Backing up current database..."

        # Get database password from environment
        local db_password
        if [[ -f "${DOCKER_DIR}/.env.prod" ]]; then
            db_password=$(grep MYSQL_ROOT_PASSWORD "${DOCKER_DIR}/.env.prod" | cut -d'=' -f2)
        else
            warn "⚠️ Could not find database password, using default"
            db_password="123456"
        fi

        docker exec weblog-mysql-prod mysqldump \
            -u root -p"$db_password" \
            --single-transaction \
            --routines \
            --triggers \
            weblog > "${pre_rollback_dir}/current_database.sql" 2>/dev/null || warn "Current database backup failed"
    fi

    # Backup current configuration
    cp -r "${DOCKER_DIR}"/*.yml "${pre_rollback_dir}/" 2>/dev/null || true
    cp "${DOCKER_DIR}/.env."* "${pre_rollback_dir}/" 2>/dev/null || true

    log "✅ Pre-rollback backup created: $pre_rollback_dir"
    export PRE_ROLLBACK_BACKUP="$pre_rollback_dir"
}

# Stop current services
stop_current_services() {
    log "🛑 Stopping current services..."

    cd "$DOCKER_DIR"

    # Try to stop services gracefully
    if [[ -f "docker-compose.yml" ]] || [[ -f "docker-compose.prod.yml" ]]; then
        local compose_file="docker-compose.yml"
        [[ -f "docker-compose.prod.yml" ]] && compose_file="docker-compose.prod.yml"

        docker-compose -f "$compose_file" down --timeout 30 || warn "Some services failed to stop gracefully"
    else
        warn "No docker-compose file found, stopping containers manually"
        docker stop $(docker ps --format '{{.Names}}' | grep weblog) 2>/dev/null || true
    fi

    log "✅ Services stopped"
}

# Restore configuration files
restore_configuration() {
    log "📄 Restoring configuration files..."

    cd "$DOCKER_DIR"

    # Restore docker-compose files
    if [[ -f "${BACKUP_PATH}/docker-compose.yml" ]]; then
        cp "${BACKUP_PATH}/docker-compose.yml" ./ || warn "Failed to restore docker-compose.yml"
        log "  ✅ Restored: docker-compose.yml"
    fi

    if [[ -f "${BACKUP_PATH}/docker-compose.prod.yml" ]]; then
        cp "${BACKUP_PATH}/docker-compose.prod.yml" ./ || warn "Failed to restore docker-compose.prod.yml"
        log "  ✅ Restored: docker-compose.prod.yml"
    fi

    # Restore environment files
    for env_file in "${BACKUP_PATH}"/.env.*; do
        if [[ -f "$env_file" ]]; then
            cp "$env_file" ./ || warn "Failed to restore $(basename "$env_file")"
            log "  ✅ Restored: $(basename "$env_file")"
        fi
    done

    log "✅ Configuration restored"
}

# Restore database
restore_database() {
    if [[ ! -f "${BACKUP_PATH}/database_backup.sql" ]]; then
        warn "⚠️ Database backup not found, skipping database restore"
        return 0
    fi

    log "🗄️ Restoring database..."

    # Start only MySQL container first
    docker-compose up -d mysql || error "Failed to start MySQL container"

    # Wait for MySQL to be ready
    local max_wait=60
    local wait_time=0

    while ! docker exec weblog-mysql-prod mysqladmin ping -h localhost --silent 2>/dev/null; do
        if [[ $wait_time -ge $max_wait ]]; then
            error "MySQL failed to start within $max_wait seconds"
        fi

        log "  Waiting for MySQL to be ready... (${wait_time}s)"
        sleep 5
        wait_time=$((wait_time + 5))
    done

    # Get database password
    local db_password
    if [[ -f "${DOCKER_DIR}/.env.prod" ]]; then
        db_password=$(grep MYSQL_ROOT_PASSWORD "${DOCKER_DIR}/.env.prod" | cut -d'=' -f2)
    else
        db_password="123456"
    fi

    # Drop and recreate database
    log "  Recreating database..."
    docker exec weblog-mysql-prod mysql -u root -p"$db_password" -e "DROP DATABASE IF EXISTS weblog; CREATE DATABASE weblog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" || error "Failed to recreate database"

    # Restore database from backup
    log "  Importing database backup..."
    docker exec -i weblog-mysql-prod mysql -u root -p"$db_password" weblog < "${BACKUP_PATH}/database_backup.sql" || error "Database restore failed"

    log "✅ Database restored"
}

# Restore MinIO data
restore_minio() {
    if [[ ! -f "${BACKUP_PATH}/minio_backup.tar.gz" ]]; then
        warn "⚠️ MinIO backup not found, skipping MinIO restore"
        return 0
    fi

    log "🗄️ Restoring MinIO data..."

    # Start MinIO container
    docker-compose up -d minio || error "Failed to start MinIO container"

    # Wait for MinIO to be ready
    sleep 10

    # Restore MinIO data
    docker exec weblog-minio-prod sh -c 'rm -rf /data/* && tar -xzf - -C /' < "${BACKUP_PATH}/minio_backup.tar.gz" || warn "MinIO restore failed"

    log "✅ MinIO data restored"
}

# Start all services
start_services() {
    log "🚀 Starting all services..."

    cd "$DOCKER_DIR"

    # Determine compose file
    local compose_file="docker-compose.yml"
    [[ -f "docker-compose.prod.yml" ]] && compose_file="docker-compose.prod.yml"

    # Start all services
    docker-compose -f "$compose_file" up -d || error "Failed to start services"

    log "✅ Services started"
}

# Verify rollback
verify_rollback() {
    log "🏥 Verifying rollback..."

    local max_retries=30
    local retry_delay=10

    # Check backend health
    for i in $(seq 1 $max_retries); do
        if curl -sf http://localhost:8080/actuator/health >/dev/null 2>&1; then
            log "  ✅ Backend is healthy"
            break
        fi

        if [[ $i -eq $max_retries ]]; then
            error "❌ Backend health check failed after rollback"
        fi

        log "    Waiting for backend... (attempt $i/$max_retries)"
        sleep $retry_delay
    done

    # Check frontend
    if curl -sf http://localhost >/dev/null 2>&1; then
        log "  ✅ Frontend is healthy"
    else
        warn "⚠️ Frontend health check failed"
    fi

    # Check MinIO
    if curl -sf http://localhost:9000/minio/health/live >/dev/null 2>&1; then
        log "  ✅ MinIO is healthy"
    else
        warn "⚠️ MinIO health check failed"
    fi

    log "✅ Rollback verification completed"
}

# Display rollback summary
display_summary() {
    log "📊 Rollback Summary:"
    echo
    echo "🔄 Rollback Details:"
    echo "   Backup Used:    $BACKUP_TIMESTAMP"
    echo "   Backup Path:    $BACKUP_PATH"
    echo "   Rollback Time:  $(date)"
    if [[ -n "${PRE_ROLLBACK_BACKUP:-}" ]]; then
        echo "   Pre-rollback:   $PRE_ROLLBACK_BACKUP"
    fi
    echo
    echo "🌐 Access Points:"
    echo "   Frontend:      http://localhost"
    echo "   Backend API:   http://localhost:8080"
    echo "   MinIO Console: http://localhost:9001"
    echo
    echo "📊 Container Status:"
    docker-compose ps
    echo
}

# Cleanup function
cleanup() {
    log "🧹 Cleaning up..."

    # Remove any temporary files
    # (Add cleanup tasks if needed)

    log "✅ Cleanup completed"
}

# List available backups
list_backups() {
    local backup_dir="${DOCKER_DIR}/backups"

    if [[ ! -d "$backup_dir" ]]; then
        error "❌ Backup directory not found: $backup_dir"
    fi

    echo "📦 Available Backups:"
    echo

    for backup in $(ls -1 "$backup_dir" | grep -E '^[0-9]{8}_[0-9]{6}$' | sort -r); do
        local backup_path="${backup_dir}/${backup}"
        local backup_date=$(echo "$backup" | sed 's/_/ /')
        local backup_size=$(du -sh "$backup_path" | cut -f1)

        echo "   $backup  ($backup_date)  [$backup_size]"

        # Show what's in this backup
        if [[ -f "${backup_path}/database_backup.sql" ]]; then
            echo "     ✅ Database backup"
        else
            echo "     ❌ No database backup"
        fi

        if [[ -f "${backup_path}/minio_backup.tar.gz" ]]; then
            echo "     ✅ MinIO backup"
        else
            echo "     ❌ No MinIO backup"
        fi

        echo
    done
}

# Help function
show_help() {
    cat << EOF
Weblog Application Rollback Script

USAGE:
    $0 [OPTIONS] [BACKUP_TIMESTAMP]

OPTIONS:
    -l, --list      List available backups
    -h, --help      Show this help message

ARGUMENTS:
    BACKUP_TIMESTAMP    Specific backup to rollback to (format: YYYYMMDD_HHMMSS)
                       If not provided, uses the most recent backup

EXAMPLES:
    $0                      # Rollback to most recent backup
    $0 20240115_143022      # Rollback to specific backup
    $0 --list               # List available backups

NOTES:
    - This script will create a pre-rollback backup of the current state
    - All services will be stopped during the rollback process
    - Database and MinIO data will be restored if backups are available
    - Configuration files will be restored to the backup state

WARNING:
    This operation will replace current data with backup data.
    Make sure you want to proceed before running this script.

EOF
}

# Signal handlers
trap 'error "Rollback interrupted"' INT TERM
trap 'cleanup' EXIT

# Main rollback workflow
main() {
    log "🔄 Starting rollback process..."
    echo

    find_latest_backup
    validate_backup

    # Confirm rollback
    echo
    warn "⚠️  ROLLBACK CONFIRMATION"
    warn "   This will replace current data with backup from: $BACKUP_TIMESTAMP"
    warn "   Current state will be backed up before rollback"
    echo
    read -p "   Do you want to continue? (yes/no): " -r
    if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
        info "Rollback cancelled"
        exit 0
    fi
    echo

    create_pre_rollback_backup
    stop_current_services
    restore_configuration
    restore_database
    restore_minio
    start_services
    verify_rollback
    display_summary

    echo
    log "✅ Rollback completed successfully!"
    log "🕒 Total time: $((SECONDS / 60))m $((SECONDS % 60))s"
}

# Parse command line arguments
case "${1:-}" in
    -l|--list)
        list_backups
        exit 0
        ;;
    -h|--help)
        show_help
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac