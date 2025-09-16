# Weblog Project - Session Notes

## Project Overview
This is a full-stack blogging platform with:
- **Backend**: Spring Boot 2.6.3 multi-module Maven project
  - `weblog-web`: Main entry point
  - `weblog-module-admin`: Admin functionality
  - `weblog-module-common`: Shared utilities
  - `weblog-module-jwt`: JWT authentication
- **Frontend**: Vue 3 + Vite application with Element Plus and Tailwind CSS
- **Storage**: MinIO for file/image storage

## Issue: MinIO Images Not Displaying on Frontend

### Problem Description
Images uploaded to MinIO storage are not showing up on the frontend webpage.

### Root Cause
Cross-Origin Resource Sharing (CORS) issue - Frontend running on different port cannot directly access MinIO server.

### Technical Details
1. **Backend Configuration**:
   - MinIO endpoint: `http://127.0.0.1:9000`
   - Bucket name: `weblog`
   - Files uploaded successfully to MinIO
   - Backend returns direct MinIO URLs (e.g., `http://127.0.0.1:9000/weblog/filename.jpg`)

2. **Frontend Configuration**:
   - Vue app likely running on port 5173
   - API calls proxied through `/api` to `http://localhost:8080`
   - Images trying to load directly from MinIO URL

3. **Issue**: Browser blocks cross-origin requests from frontend (port 5173) to MinIO (port 9000)

### Solutions

#### Option 1: Configure MinIO CORS (Recommended)
```bash
# Create CORS configuration
cat > cors.json <<EOF
{
  "CORSRules": [
    {
      "AllowedOrigins": ["http://localhost:5173", "http://localhost:3000", "*"],
      "AllowedMethods": ["GET", "HEAD"],
      "AllowedHeaders": ["*"],
      "ExposeHeaders": ["ETag"],
      "MaxAgeSeconds": 3000
    }
  ]
}
EOF

# Apply CORS to bucket
mc alias set myminio http://127.0.0.1:9000 minioadmin minioadmin
mc anonymous set download myminio/weblog
mc cors set cors.json myminio/weblog
```

#### Option 2: Proxy Through Spring Boot
Create an endpoint to serve images through the backend API.

#### Option 3: Make Bucket Public (Development Only)
```bash
mc policy set public myminio/weblog
```

## Key Files Referenced
- `/weblog-springboot/weblog-module-admin/src/main/java/com/luqi/weblog/admin/utils/MinioUtil.java` - File upload logic
- `/weblog-springboot/weblog-module-admin/src/main/java/com/luqi/weblog/admin/config/MinioProperties.java` - MinIO configuration
- `/weblog-springboot/weblog-web/src/main/resources/application-dev.yml` - MinIO connection settings
- `/blog-vue3/vite.config.js` - Frontend proxy configuration
- `/blog-vue3/src/pages/frontend/` - Frontend pages displaying images

## Next Steps
1. Choose and implement one of the solutions above
2. Test image display on frontend
3. Consider implementing proper image optimization and CDN for production