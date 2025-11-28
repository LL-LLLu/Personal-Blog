#!/bin/bash

# AWS EC2 Connection Script for Weblog Server
# Usage: ./connect-aws.sh [environment]
# Example: ./connect-aws.sh prod

# ==============================================================================
# CONFIGURATION - Update these values with your AWS instance details
# ==============================================================================

# Production server
PROD_IP="98.82.37.203"
PROD_KEY="~/Documents/Important/personal_blog/Personal_Blog.pem"
PROD_USER="ubuntu"
PROD_NAME="Weblog Production Server"

# Staging server (example)
STAGING_IP="YOUR_STAGING_IP_HERE"
STAGING_KEY="~/Downloads/weblog-staging-key.pem"
STAGING_USER="ubuntu"
STAGING_NAME="Weblog Staging Server"

# Development server (example)
DEV_IP="YOUR_DEV_IP_HERE"
DEV_KEY="~/Downloads/weblog-dev-key.pem"
DEV_USER="ubuntu"
DEV_NAME="Weblog Development Server"

# Default environment
DEFAULT_ENV="prod"

# SSH options
SSH_OPTIONS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o LogLevel=ERROR"

# ==============================================================================
# COLORS FOR OUTPUT
# ==============================================================================
RED=$'\033[0;31m'
GREEN=$'\033[0;32m'
YELLOW=$'\033[1;33m'
BLUE=$'\033[0;34m'
PURPLE=$'\033[0;35m'
CYAN=$'\033[0;36m'
NC=$'\033[0m' # No Color

# ==============================================================================
# FUNCTIONS
# ==============================================================================

# Print colored output
print_color() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Print banner
print_banner() {
    echo
    print_color "$CYAN" "╔════════════════════════════════════════════════╗"
    print_color "$CYAN" "║          AWS EC2 Connection Script             ║"
    print_color "$CYAN" "║              Weblog Project                    ║"
    print_color "$CYAN" "╚════════════════════════════════════════════════╝"
    echo
}

# Get server config based on environment
get_server_config() {
    local env=$1
    local config_type=$2

    case "$env" in
        prod)
            case "$config_type" in
                ip) echo "$PROD_IP" ;;
                key) echo "$PROD_KEY" ;;
                user) echo "$PROD_USER" ;;
                name) echo "$PROD_NAME" ;;
            esac
            ;;
        staging)
            case "$config_type" in
                ip) echo "$STAGING_IP" ;;
                key) echo "$STAGING_KEY" ;;
                user) echo "$STAGING_USER" ;;
                name) echo "$STAGING_NAME" ;;
            esac
            ;;
        dev)
            case "$config_type" in
                ip) echo "$DEV_IP" ;;
                key) echo "$DEV_KEY" ;;
                user) echo "$DEV_USER" ;;
                name) echo "$DEV_NAME" ;;
            esac
            ;;
        *)
            echo ""
            ;;
    esac
}

# Check if required files exist
check_prerequisites() {
    local env=$1
    local key_path=$(get_server_config "$env" "key")

    # Expand tilde to home directory
    key_path="${key_path/#\~/$HOME}"

    if [ ! -f "$key_path" ]; then
        print_color "$RED" "❌ Error: Key file not found: $key_path"
        print_color "$YELLOW" "Please ensure your .pem file is in the correct location"
        exit 1
    fi

    # Check and fix key permissions
    local current_perms=$(stat -f "%OLp" "$key_path" 2>/dev/null || stat -c "%a" "$key_path" 2>/dev/null)
    if [ "$current_perms" != "400" ]; then
        print_color "$YELLOW" "⚠️  Fixing key permissions (current: $current_perms, required: 400)"
        chmod 400 "$key_path"
        print_color "$GREEN" "✅ Key permissions fixed"
    fi
}

# Show available environments
show_environments() {
    print_color "$BLUE" "Available environments:"
    echo

    for env in prod staging dev; do
        local ip=$(get_server_config "$env" "ip")
        local name=$(get_server_config "$env" "name")

        if [ "$ip" != "" ] && [[ "$ip" != *"YOUR_"* ]]; then
            print_color "$GREEN" "  • $env - $name ($ip)"
        fi
    done
    echo
}

# Connect to server
connect_to_server() {
    local env=$1
    local ip=$(get_server_config "$env" "ip")
    local key=$(get_server_config "$env" "key")
    local user=$(get_server_config "$env" "user")
    local name=$(get_server_config "$env" "name")

    # Expand tilde to home directory
    key="${key/#\~/$HOME}"

    if [ -z "$ip" ] || [[ "$ip" == *"YOUR_"* ]]; then
        print_color "$RED" "❌ Error: Server IP not configured for environment: $env"
        print_color "$YELLOW" "Please edit this script and update the IP address"
        exit 1
    fi

    print_color "$GREEN" "🚀 Connecting to $name..."
    print_color "$CYAN" "   Environment: $env"
    print_color "$CYAN" "   IP: $ip"
    print_color "$CYAN" "   User: $user"
    print_color "$CYAN" "   Key: $key"
    echo

    # Show quick commands
    print_color "$PURPLE" "📝 Quick Commands (after connecting):"
    print_color "$YELLOW" "   docker ps                    # List running containers"
    print_color "$YELLOW" "   docker logs weblog-backend   # View backend logs"
    print_color "$YELLOW" "   docker logs weblog-mysql     # View database logs"
    print_color "$YELLOW" "   docker logs weblog-minio     # View MinIO logs"
    print_color "$YELLOW" "   docker exec -it weblog-mysql mysql -u root -p  # Access MySQL"
    print_color "$YELLOW" "   sudo tail -f /var/log/nginx/access.log        # View Nginx logs"
    echo
    print_color "$GREEN" "Connecting..."
    echo

    # Connect via SSH
    ssh $SSH_OPTIONS -i "$key" "${user}@${ip}"
}

# Quick server health check
health_check() {
    local env=$1
    local ip=$(get_server_config "$env" "ip")
    local key=$(get_server_config "$env" "key")
    local user=$(get_server_config "$env" "user")
    local name=$(get_server_config "$env" "name")

    # Expand tilde to home directory
    key="${key/#\~/$HOME}"

    print_color "$BLUE" "🏥 Running health check on $name..."
    echo

    ssh $SSH_OPTIONS -i "$key" "${user}@${ip}" << 'EOF'
        echo "=== System Info ==="
        uname -a
        echo

        echo "=== Disk Usage ==="
        df -h | grep -E '^/dev/'
        echo

        echo "=== Memory Usage ==="
        free -h
        echo

        echo "=== Docker Containers ==="
        docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
        echo

        echo "=== Service Status ==="
        echo -n "Nginx: "
        systemctl is-active nginx || echo "not running"
        echo -n "Docker: "
        systemctl is-active docker || echo "not running"
        echo

        echo "=== Recent Errors (last 10 lines) ==="
        sudo journalctl -p err -n 10 --no-pager 2>/dev/null || echo "No recent errors"
EOF
}

# Copy file to server
copy_to_server() {
    local env=$1
    local local_path=$2
    local remote_path=$3

    local ip=$(get_server_config "$env" "ip")
    local key=$(get_server_config "$env" "key")
    local user=$(get_server_config "$env" "user")

    # Expand tilde to home directory
    key="${key/#\~/$HOME}"

    print_color "$BLUE" "📤 Copying file to server..."
    scp -i "$key" "$local_path" "${user}@${ip}:${remote_path}"
    print_color "$GREEN" "✅ File copied successfully"
}

# Execute command on server
execute_command() {
    local env=$1
    shift
    local command="$@"

    local ip=$(get_server_config "$env" "ip")
    local key=$(get_server_config "$env" "key")
    local user=$(get_server_config "$env" "user")

    # Expand tilde to home directory
    key="${key/#\~/$HOME}"

    print_color "$BLUE" "⚡ Executing command on server..."
    ssh $SSH_OPTIONS -i "$key" "${user}@${ip}" "$command"
}

# Show help
show_help() {
    print_banner
    print_color "$GREEN" "Usage: $0 [environment] [command]"
    echo
    print_color "$BLUE" "Environments:"
    print_color "$YELLOW" "  prod     - Production server (default)"
    print_color "$YELLOW" "  staging  - Staging server"
    print_color "$YELLOW" "  dev      - Development server"
    echo
    print_color "$BLUE" "Commands:"
    print_color "$YELLOW" "  connect  - Connect to server (default)"
    print_color "$YELLOW" "  health   - Run health check"
    print_color "$YELLOW" "  copy     - Copy file to server"
    print_color "$YELLOW" "  exec     - Execute command on server"
    print_color "$YELLOW" "  list     - List configured servers"
    print_color "$YELLOW" "  help     - Show this help"
    echo
    print_color "$BLUE" "Examples:"
    print_color "$CYAN" "  $0                    # Connect to production"
    print_color "$CYAN" "  $0 prod              # Connect to production"
    print_color "$CYAN" "  $0 staging           # Connect to staging"
    print_color "$CYAN" "  $0 prod health       # Health check on production"
    print_color "$CYAN" "  $0 prod copy local.txt /tmp/  # Copy file"
    print_color "$CYAN" "  $0 prod exec docker ps        # Execute command"
    echo
    print_color "$PURPLE" "Configuration:"
    print_color "$YELLOW" "  Edit this script to update server IPs and key paths"
    echo
}

# ==============================================================================
# MAIN SCRIPT
# ==============================================================================

# Parse arguments
ENVIRONMENT="${1:-$DEFAULT_ENV}"
COMMAND="${2:-connect}"

# Handle help
if [ "$ENVIRONMENT" == "help" ] || [ "$ENVIRONMENT" == "-h" ] || [ "$ENVIRONMENT" == "--help" ]; then
    show_help
    exit 0
fi

# Handle list command
if [ "$ENVIRONMENT" == "list" ]; then
    print_banner
    show_environments
    exit 0
fi

# Validate environment
valid_env=""
for env in prod staging dev; do
    if [ "$ENVIRONMENT" == "$env" ]; then
        valid_env="$env"
        break
    fi
done

if [ -z "$valid_env" ]; then
    print_color "$RED" "❌ Error: Unknown environment: $ENVIRONMENT"
    echo
    show_environments
    exit 1
fi

# Print banner for connect command
if [ "$COMMAND" == "connect" ]; then
    print_banner
fi

# Check prerequisites
check_prerequisites "$ENVIRONMENT"

# Execute command
case "$COMMAND" in
    connect)
        connect_to_server "$ENVIRONMENT"
        ;;
    health)
        health_check "$ENVIRONMENT"
        ;;
    copy)
        if [ -z "$3" ] || [ -z "$4" ]; then
            print_color "$RED" "❌ Error: copy requires source and destination"
            print_color "$YELLOW" "Usage: $0 $ENVIRONMENT copy <local_file> <remote_path>"
            exit 1
        fi
        copy_to_server "$ENVIRONMENT" "$3" "$4"
        ;;
    exec)
        if [ -z "$3" ]; then
            print_color "$RED" "❌ Error: exec requires a command"
            print_color "$YELLOW" "Usage: $0 $ENVIRONMENT exec <command>"
            exit 1
        fi
        shift 2
        execute_command "$ENVIRONMENT" "$@"
        ;;
    *)
        print_color "$RED" "❌ Error: Unknown command: $COMMAND"
        show_help
        exit 1
        ;;
esac

# Exit message
echo
print_color "$GREEN" "👋 Session ended"