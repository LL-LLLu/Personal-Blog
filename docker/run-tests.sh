#!/bin/bash

# Weblog Comprehensive Test Suite Runner
# This script sets up the environment and runs all blog functionality tests

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print banner
echo -e "${BLUE}"
echo "=================================================================="
echo "           WEBLOG COMPREHENSIVE TEST SUITE"
echo "=================================================================="
echo -e "${NC}"

# Check if we're in the correct directory
if [ ! -f "test-blog-functionality.js" ]; then
    echo -e "${RED}❌ Error: test-blog-functionality.js not found${NC}"
    echo "Please run this script from the directory containing the test files"
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Error: Node.js is not installed${NC}"
    echo "Please install Node.js from https://nodejs.org/"
    exit 1
fi

echo -e "${YELLOW}📋 Pre-test Checklist${NC}"
echo "Checking system requirements..."

# Check if backend is running
echo -n "Backend (localhost:8080): "
if curl -s --connect-timeout 5 http://localhost:8080/actuator/health > /dev/null 2>&1 || \
   curl -s --connect-timeout 5 http://localhost:8080/ > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Running${NC}"
else
    echo -e "${RED}❌ Not accessible${NC}"
    echo -e "${YELLOW}⚠️  Backend may not be running. Tests may fail.${NC}"
    echo "Please ensure the Spring Boot backend is running on port 8080"
fi

# Check if frontend is running
echo -n "Frontend (localhost:80): "
if curl -s --connect-timeout 5 http://localhost:80/ > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Running${NC}"
else
    echo -e "${RED}❌ Not accessible${NC}"
    echo -e "${YELLOW}⚠️  Frontend may not be running. Some tests may fail.${NC}"
    echo "Please ensure the frontend is running on port 80"
fi

# Check if MinIO is running
echo -n "MinIO (localhost:9000): "
if curl -s --connect-timeout 5 http://localhost:9000/minio/health/live > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Running${NC}"
else
    echo -e "${RED}❌ Not accessible${NC}"
    echo -e "${YELLOW}⚠️  MinIO may not be running. File upload tests may fail.${NC}"
    echo "Please ensure MinIO is running on port 9000"
fi

# Check if MySQL is running (via backend connection)
echo -n "Database: "
if curl -s --connect-timeout 5 http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Backend connected${NC}"
else
    echo -e "${YELLOW}⚠️  Cannot verify database connection${NC}"
fi

echo ""

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}📦 Installing Node.js dependencies...${NC}"
    npm install
    echo ""
fi

# Ask for confirmation to proceed
echo -e "${YELLOW}🚀 Ready to run tests. Continue? (y/N)${NC}"
read -r response
case "$response" in
    [yY][eE][sS]|[yY])
        echo -e "${GREEN}Starting tests...${NC}"
        echo ""
        ;;
    *)
        echo -e "${YELLOW}Tests cancelled by user.${NC}"
        exit 0
        ;;
esac

# Run the tests
echo -e "${BLUE}🧪 Executing Test Suite...${NC}"
echo ""

# Record start time
start_time=$(date +%s)

# Run the actual tests
if node test-blog-functionality.js; then
    test_result="PASSED"
    result_color=$GREEN
else
    test_result="FAILED"
    result_color=$RED
fi

# Calculate duration
end_time=$(date +%s)
duration=$((end_time - start_time))

# Print final results
echo ""
echo -e "${BLUE}=================================================================="
echo "                    TEST EXECUTION COMPLETE"
echo "==================================================================${NC}"
echo -e "Result: ${result_color}${test_result}${NC}"
echo "Duration: ${duration} seconds"
echo "Timestamp: $(date)"

if [ "$test_result" = "FAILED" ]; then
    echo ""
    echo -e "${RED}❌ Some tests failed. Please check the output above for details.${NC}"
    echo ""
    echo -e "${YELLOW}Common troubleshooting steps:${NC}"
    echo "1. Ensure all services are running (backend, frontend, MinIO, MySQL)"
    echo "2. Check admin credentials (default: admin/123456)"
    echo "3. Verify database connectivity"
    echo "4. Check network connectivity between services"
    echo "5. Review application logs for errors"
    echo ""
    exit 1
else
    echo ""
    echo -e "${GREEN}✅ All tests passed successfully!${NC}"
    echo "Your weblog application is functioning correctly."
    echo ""
    exit 0
fi