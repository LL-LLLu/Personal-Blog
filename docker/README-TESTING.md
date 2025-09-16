# Weblog Comprehensive Test Suite

This directory contains a comprehensive test suite that validates all functionalities of the weblog application.

## 🎯 What Does This Test Suite Cover?

### 🔐 Authentication & Security
- ✅ Admin login with valid credentials
- ✅ Invalid login rejection
- ✅ JWT token validation
- ✅ Protected endpoint access control
- ✅ Unauthorized access rejection

### 📊 Dashboard & Statistics
- ✅ Basic dashboard statistics (articles, categories, tags, PV counts)
- ✅ Article publish heatmap data
- ✅ Weekly page view statistics
- ✅ Data aggregation accuracy

### 📝 Article Management (CRUD)
- ✅ Publish new articles
- ✅ Update existing articles
- ✅ Delete articles
- ✅ Get article lists (admin)
- ✅ Get article details
- ✅ Article validation

### 📁 Category Management
- ✅ Add new categories
- ✅ Delete categories
- ✅ Get category lists
- ✅ Category dropdown options

### 🏷️ Tag Management
- ✅ Add new tags
- ✅ Delete tags
- ✅ Search tags
- ✅ Get tag lists
- ✅ Tag dropdown options

### 🌐 Public APIs
- ✅ Frontend article lists
- ✅ Public category lists
- ✅ Public tag lists
- ✅ Blog settings
- ✅ Statistics information
- ✅ Archive functionality

### ⚙️ Blog Settings
- ✅ Get blog configuration
- ✅ Admin settings access

### 🖥️ Frontend Accessibility
- ✅ Homepage accessibility
- ✅ Frontend service availability

## 🚀 Quick Start

### Prerequisites
- Node.js (v14 or higher)
- All blog services running:
  - Backend (Spring Boot) on http://localhost:8080
  - Frontend (Vue.js) on http://localhost:80
  - MinIO on http://localhost:9000
  - MySQL database accessible

### Running the Tests

#### Option 1: Using the Shell Script (Recommended)
```bash
cd /Users/qilu/Code/IDEA_Projects/docker
./run-tests.sh
```

#### Option 2: Using Node.js Directly
```bash
cd /Users/qilu/Code/IDEA_Projects/docker
npm install
node test-blog-functionality.js
```

#### Option 3: Using npm Scripts
```bash
cd /Users/qilu/Code/IDEA_Projects/docker
npm install
npm test
```

## 📋 Test Configuration

The test script uses these default configurations:

```javascript
{
    API_BASE: 'http://localhost:8080',        // Backend API
    FRONTEND_BASE: 'http://localhost:80',     // Frontend URL
    ADMIN_CREDENTIALS: {
        username: 'admin',
        password: '123456'
    }
}
```

### Customizing Test Configuration

You can modify the configuration in `test-blog-functionality.js`:

```javascript
const CONFIG = {
    API_BASE: 'http://your-backend-url:port',
    FRONTEND_BASE: 'http://your-frontend-url:port',
    ADMIN_CREDENTIALS: {
        username: 'your-admin-username',
        password: 'your-admin-password'
    }
};
```

## 📊 Understanding Test Results

### Success Output
```
✅ Admin Login
✅ Get User Info
✅ Basic Dashboard Statistics
✅ Add Category
✅ Delete Test Category
... (all tests passing)

==============================================================
TEST SUMMARY
==============================================================
Total Tests: 25
Passed: 25
Failed: 0
Success Rate: 100.0%
```

### Failure Output
```
❌ Admin Login: Status: 401, Response: {"success":false,"message":"Invalid credentials"}
❌ Get User Info: Status: 401

==============================================================
TEST SUMMARY
==============================================================
Total Tests: 25
Passed: 20
Failed: 5
Success Rate: 80.0%

FAILED TESTS:
- Admin Login: Status: 401, Response: {"success":false,"message":"Invalid credentials"}
- Get User Info: Status: 401
```

## 🐛 Troubleshooting

### Common Issues and Solutions

#### 1. "Cannot connect to backend"
```bash
# Check if backend is running
curl http://localhost:8080/actuator/health
# or
curl http://localhost:8080/

# Start backend if not running
cd /Users/qilu/Code/IDEA_Projects/weblog/weblog-springboot
mvn spring-boot:run
# or using Docker
docker-compose up backend
```

#### 2. "Authentication failed"
- Verify admin credentials in the database
- Check if the admin user exists and has the correct role (`ROLE_ADMIN`)
- Ensure password is correctly hashed

#### 3. "Frontend not accessible"
```bash
# Check if frontend is running
curl http://localhost:80/

# Start frontend if not running
cd /Users/qilu/Code/IDEA_Projects/blog-vue3
npm run dev
# or using Docker
docker-compose up frontend
```

#### 4. "MinIO tests failing"
```bash
# Check MinIO status
curl http://localhost:9000/minio/health/live

# Start MinIO if not running
docker-compose up minio
```

#### 5. "Database connection issues"
- Verify MySQL is running
- Check database credentials in application configuration
- Ensure database schema is properly initialized

## 🔧 Advanced Usage

### Running Specific Test Categories

You can modify the `test-blog-functionality.js` file to run only specific test categories by commenting out unwanted test functions in the `runAllTests()` function:

```javascript
async function runAllTests() {
    // Authentication tests
    await testLogin();
    // await testDashboardStatistics();  // Disable dashboard tests
    // await testArticleManagement();    // Disable article tests
    await testPublicAPIs();              // Keep public API tests
}
```

### Adding Custom Tests

You can add your own test functions:

```javascript
async function testCustomFunctionality() {
    console.log('\n🔧 Testing Custom Functionality...');

    try {
        const options = createRequestOptions('/your/custom/endpoint', 'POST');
        const response = await makeRequest(options, { your: 'data' });

        logTest('Custom Test Name',
            response.statusCode === 200 && response.body.success,
            response.statusCode !== 200 ? `Status: ${response.statusCode}` : '');
    } catch (error) {
        logTest('Custom Test Name', false, error.message);
    }
}
```

### Environment-Specific Testing

For different environments (development, staging, production), create environment-specific configuration files:

```javascript
// config/development.js
module.exports = {
    API_BASE: 'http://localhost:8080',
    FRONTEND_BASE: 'http://localhost:80',
    ADMIN_CREDENTIALS: { username: 'admin', password: '123456' }
};

// config/production.js
module.exports = {
    API_BASE: 'https://your-production-api.com',
    FRONTEND_BASE: 'https://your-blog.com',
    ADMIN_CREDENTIALS: { username: 'admin', password: 'secure-password' }
};
```

## 📝 Test Coverage Summary

| Component | Coverage | Tests |
|-----------|----------|-------|
| Authentication | ✅ Complete | Login, Token validation, Access control |
| Article Management | ✅ Complete | CRUD operations, Validation |
| Category Management | ✅ Complete | CRUD operations, Relationships |
| Tag Management | ✅ Complete | CRUD operations, Search |
| Dashboard | ✅ Complete | Statistics, Aggregations |
| Public APIs | ✅ Complete | Frontend endpoints |
| File Upload | ⚠️ Partial | Upload endpoint (no file validation) |
| Frontend | ✅ Complete | Accessibility checks |

## 🔄 Continuous Integration

To integrate with CI/CD pipelines, the test script returns appropriate exit codes:
- `0`: All tests passed
- `1`: Some tests failed

Example GitHub Actions workflow:
```yaml
name: Blog Functionality Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-node@v2
        with:
          node-version: '16'
      - run: npm install
        working-directory: ./docker
      - run: node test-blog-functionality.js
        working-directory: ./docker
```

## 📞 Support

If you encounter issues with the test suite:

1. **Check Prerequisites**: Ensure all services are running
2. **Review Logs**: Check application logs for errors
3. **Verify Configuration**: Confirm URLs and credentials
4. **Network Issues**: Test connectivity between services
5. **Update Dependencies**: Ensure Node.js and npm packages are current

## 🎉 Success!

If all tests pass, your weblog application is functioning correctly and ready for use!

The test suite validates:
- ✅ All API endpoints are working
- ✅ Authentication and authorization are secure
- ✅ Database operations are successful
- ✅ Frontend is accessible
- ✅ All major features are functional