# 🪣 AWS S3 Bucket Setup Guide for Weblog Project

## 📋 Table of Contents
1. [Overview & Use Cases](#overview--use-cases)
2. [S3 Bucket Creation](#s3-bucket-creation)
3. [Bucket Configuration](#bucket-configuration)
4. [IAM User & Permissions Setup](#iam-user--permissions-setup)
5. [Spring Boot Integration](#spring-boot-integration)
6. [Frontend Integration](#frontend-integration)
7. [CDN Setup with CloudFront](#cdn-setup-with-cloudfront)
8. [Security Best Practices](#security-best-practices)
9. [Monitoring & Logging](#monitoring--logging)
10. [Cost Optimization](#cost-optimization)
11. [Troubleshooting](#troubleshooting)

---

## 🎯 Overview & Use Cases

### What S3 Will Store for Your Weblog Project

**Primary Use Cases:**
- 📸 **Blog Images**: Article featured images, inline images, galleries
- 📄 **Document Uploads**: PDFs, documents attached to articles
- 🎨 **Static Assets**: CSS, JS files for CDN distribution
- 💾 **Backup Storage**: Database backups, configuration backups
- 📊 **Log Files**: Application logs, access logs archival
- 👤 **User Avatars**: Profile pictures and user-generated content

**Benefits of Using S3:**
- ✅ **Scalability**: Unlimited storage capacity
- ✅ **Cost-Effective**: Pay only for what you use
- ✅ **Reliability**: 99.999999999% (11 9's) durability
- ✅ **Global CDN**: Fast content delivery worldwide
- ✅ **Security**: Fine-grained access controls
- ✅ **Integration**: Native AWS service integration

---

## 🪣 S3 Bucket Creation

### Step 1: Access S3 Console

1. **Log into AWS Console**:
   - Go to [AWS Console](https://aws.amazon.com/console/)
   - Search for "S3" or navigate to Storage → S3

2. **Create Bucket**:
   - Click "Create bucket"
   - You'll see the bucket creation wizard

### Step 2: Basic Bucket Configuration

**Bucket Settings:**

```yaml
Bucket name: weblog-storage-[your-unique-id]
# Example: weblog-storage-2024-prod
# Note: Bucket names must be globally unique across all AWS accounts
```

**AWS Region:**
```yaml
AWS Region: US East (N. Virginia) us-east-1
# Or choose the region closest to your users
# Recommended: Same region as your EC2 instance
```

**Object Ownership:**
```yaml
Object Ownership: ACLs disabled (recommended)
# This means bucket owner owns all objects
```

### Step 3: Public Access Settings

**⚠️ Important Security Configuration:**

```yaml
Block Public Access settings for this bucket:
☑️ Block all public access: UNCHECKED (for web assets)

Individual settings:
☑️ Block public access to buckets and objects granted through new access control lists (ACLs): CHECKED
☑️ Block public access to buckets and objects granted through any access control lists (ACLs): CHECKED
☑️ Block public access to buckets and objects granted through new public bucket or access point policies: UNCHECKED
☑️ Block public access to buckets and objects granted through any public bucket or access point policies: UNCHECKED
```

**Why this configuration:**
- Allows public read access to images/assets via bucket policies
- Prevents ACL-based public access (more secure)
- Enables controlled public access for web content

### Step 4: Bucket Versioning

```yaml
Bucket Versioning: Enable
# This allows you to recover from accidental deletions/overwrites
```

### Step 5: Default Encryption

```yaml
Default encryption: Server-side encryption with Amazon S3 managed keys (SSE-S3)
Encryption key type: Amazon S3 key (SSE-S3)
Bucket Key: Enable
# This encrypts all objects by default and reduces costs
```

### Step 6: Advanced Settings

```yaml
Object Lock: Disable
# Not needed for this use case

Tags (Optional):
- Key: Project, Value: weblog
- Key: Environment, Value: production
- Key: Owner, Value: your-name
```

**Click "Create bucket"**

---

## ⚙️ Bucket Configuration

### Step 1: Configure Bucket Policy

After bucket creation, configure public read access for web assets:

1. **Navigate to your bucket**
2. **Go to Permissions tab**
3. **Scroll to Bucket Policy**
4. **Click Edit and add:**

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::weblog-storage-[your-unique-id]/public/*"
        },
        {
            "Sid": "PublicReadGetObjectImages",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::weblog-storage-[your-unique-id]/images/*"
        }
    ]
}
```

**Replace `[your-unique-id]` with your actual bucket name**

### Step 2: Configure CORS

**Go to Permissions → Cross-origin resource sharing (CORS):**

```json
[
    {
        "AllowedHeaders": [
            "*"
        ],
        "AllowedMethods": [
            "GET",
            "PUT",
            "POST",
            "DELETE",
            "HEAD"
        ],
        "AllowedOrigins": [
            "http://localhost:3000",
            "http://localhost:5173",
            "http://your-domain.com",
            "https://your-domain.com",
            "http://your-ec2-ip"
        ],
        "ExposeHeaders": [
            "ETag",
            "x-amz-meta-custom-header"
        ],
        "MaxAgeSeconds": 3000
    }
]
```

**Update `AllowedOrigins` with your actual domains**

### Step 3: Create Folder Structure

Create the following folder structure in your bucket:

```
weblog-storage-[your-unique-id]/
├── images/
│   ├── articles/           # Article images
│   ├── avatars/           # User profile pictures
│   ├── thumbnails/        # Generated thumbnails
│   └── gallery/           # Image galleries
├── documents/
│   ├── uploads/           # User uploaded documents
│   └── attachments/       # Article attachments
├── public/
│   ├── css/              # Static CSS files
│   ├── js/               # Static JavaScript files
│   └── assets/           # Other static assets
├── backups/
│   ├── database/         # Database backups
│   └── configs/          # Configuration backups
└── logs/
    ├── application/      # Application logs
    └── access/           # Access logs
```

**To create folders:**
1. Click "Create folder" in your bucket
2. Enter folder name (e.g., "images")
3. Click "Create folder"
4. Repeat for all folders above

---

## 👤 IAM User & Permissions Setup

### Step 1: Create IAM User for Application

1. **Go to IAM Console**:
   - Search for "IAM" in AWS Console
   - Click "Users" in the left sidebar
   - Click "Create user"

2. **User Configuration**:
```yaml
User name: weblog-s3-user
Select AWS credential type: Access key - Programmatic access
```

### Step 2: Create Custom IAM Policy

1. **Go to IAM → Policies**
2. **Click "Create policy"**
3. **Choose JSON tab and paste:**

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "ListBucketContents",
            "Effect": "Allow",
            "Action": [
                "s3:ListBucket",
                "s3:GetBucketLocation"
            ],
            "Resource": "arn:aws:s3:::weblog-storage-[your-unique-id]"
        },
        {
            "Sid": "ManageObjects",
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:PutObject",
                "s3:DeleteObject",
                "s3:PutObjectAcl",
                "s3:GetObjectAcl"
            ],
            "Resource": "arn:aws:s3:::weblog-storage-[your-unique-id]/*"
        },
        {
            "Sid": "ManageObjectVersions",
            "Effect": "Allow",
            "Action": [
                "s3:GetObjectVersion",
                "s3:DeleteObjectVersion"
            ],
            "Resource": "arn:aws:s3:::weblog-storage-[your-unique-id]/*"
        }
    ]
}
```

**Policy Details:**
- Name: `weblog-s3-policy`
- Description: `S3 access policy for weblog application`

### Step 3: Attach Policy to User

1. **Go to IAM → Users → weblog-s3-user**
2. **Click "Add permissions"**
3. **Choose "Attach policies directly"**
4. **Search and select `weblog-s3-policy`**
5. **Click "Add permissions"**

### Step 4: Generate Access Keys

1. **Go to IAM → Users → weblog-s3-user**
2. **Click "Security credentials" tab**
3. **Click "Create access key"**
4. **Choose "Application running on an AWS compute service"**
5. **Click "Create access key"**

**⚠️ Important: Save these credentials securely:**
```
Access Key ID: AKIA...
Secret Access Key: abc123...
```

---

## 🚀 Spring Boot Integration

### Step 1: Add S3 Dependencies

Add to your `pom.xml`:

```xml
<dependencies>
    <!-- AWS SDK for Java -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
        <version>2.21.0</version>
    </dependency>

    <!-- Spring Boot AWS Starter (optional but recommended) -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-aws-starter-s3</artifactId>
        <version>3.0.3</version>
    </dependency>
</dependencies>
```

### Step 2: Configuration Properties

Add to `application-prod.yml`:

```yaml
# AWS S3 Configuration
aws:
  s3:
    bucket-name: weblog-storage-[your-unique-id]
    region: us-east-1
    access-key: ${AWS_ACCESS_KEY_ID:your-access-key}
    secret-key: ${AWS_SECRET_ACCESS_KEY:your-secret-key}

# File upload configuration
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# Custom configuration
weblog:
  file-upload:
    s3-bucket: weblog-storage-[your-unique-id]
    allowed-extensions: jpg,jpeg,png,gif,pdf,doc,docx
    max-file-size: 10485760  # 10MB in bytes
    image-base-url: https://weblog-storage-[your-unique-id].s3.amazonaws.com
```

### Step 3: S3 Service Implementation

Create `S3FileService.java`:

```java
package com.luqi.weblog.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class S3FileService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${weblog.file-upload.image-base-url}")
    private String imageBaseUrl;

    private S3Client s3Client;

    public S3Client getS3Client() {
        if (s3Client == null) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
        }
        return s3Client;
    }

    /**
     * Upload file to S3
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFileName = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        // Create S3 key (path)
        String s3Key = folder + "/" + uniqueFileName;

        try {
            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            getS3Client().putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Return public URL
            return imageBaseUrl + "/" + s3Key;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Upload article image
     */
    public String uploadArticleImage(MultipartFile file) throws IOException {
        return uploadFile(file, "images/articles");
    }

    /**
     * Upload user avatar
     */
    public String uploadUserAvatar(MultipartFile file) throws IOException {
        return uploadFile(file, "images/avatars");
    }

    /**
     * Upload document
     */
    public String uploadDocument(MultipartFile file) throws IOException {
        return uploadFile(file, "documents/uploads");
    }

    /**
     * Delete file from S3
     */
    public void deleteFile(String fileUrl) {
        try {
            // Extract S3 key from URL
            String s3Key = fileUrl.replace(imageBaseUrl + "/", "");

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            getS3Client().deleteObject(deleteObjectRequest);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String s3Key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            getS3Client().headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error checking file existence: " + e.getMessage(), e);
        }
    }
}
```

### Step 4: File Upload Controller

Create `FileUploadController.java`:

```java
package com.luqi.weblog.admin.controller;

import com.luqi.weblog.common.service.S3FileService;
import com.luqi.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin/file")
@Slf4j
public class FileUploadController {

    @Autowired
    private S3FileService s3FileService;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    /**
     * Upload article image
     */
    @PostMapping("/upload/image")
    public Response<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return Response.fail("File is empty");
            }

            if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
                return Response.fail("Invalid file type. Only JPG, PNG, GIF allowed.");
            }

            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                return Response.fail("File size too large. Maximum 10MB allowed.");
            }

            // Upload to S3
            String fileUrl = s3FileService.uploadArticleImage(file);

            log.info("File uploaded successfully: {}", fileUrl);
            return Response.success(fileUrl);

        } catch (Exception e) {
            log.error("File upload failed", e);
            return Response.fail("File upload failed: " + e.getMessage());
        }
    }

    /**
     * Upload user avatar
     */
    @PostMapping("/upload/avatar")
    public Response<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty() || !ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
                return Response.fail("Invalid image file");
            }

            String fileUrl = s3FileService.uploadUserAvatar(file);
            return Response.success(fileUrl);

        } catch (Exception e) {
            log.error("Avatar upload failed", e);
            return Response.fail("Avatar upload failed: " + e.getMessage());
        }
    }

    /**
     * Upload document
     */
    @PostMapping("/upload/document")
    public Response<?> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty() || !ALLOWED_DOCUMENT_TYPES.contains(file.getContentType())) {
                return Response.fail("Invalid document file");
            }

            String fileUrl = s3FileService.uploadDocument(file);
            return Response.success(fileUrl);

        } catch (Exception e) {
            log.error("Document upload failed", e);
            return Response.fail("Document upload failed: " + e.getMessage());
        }
    }

    /**
     * Delete file
     */
    @DeleteMapping("/delete")
    public Response<?> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            s3FileService.deleteFile(fileUrl);
            return Response.success("File deleted successfully");

        } catch (Exception e) {
            log.error("File deletion failed", e);
            return Response.fail("File deletion failed: " + e.getMessage());
        }
    }
}
```

---

## 🎨 Frontend Integration

### Step 1: Add Upload Component (Vue.js)

Create `FileUpload.vue`:

```vue
<template>
  <div class="file-upload">
    <div class="upload-area"
         :class="{ 'drag-over': isDragOver }"
         @click="triggerFileInput"
         @drop.prevent="handleDrop"
         @dragover.prevent="isDragOver = true"
         @dragleave.prevent="isDragOver = false">

      <input
        ref="fileInput"
        type="file"
        :accept="acceptedTypes"
        @change="handleFileSelect"
        style="display: none;"
        :multiple="multiple"
      />

      <div v-if="!uploading" class="upload-content">
        <i class="el-icon-upload"></i>
        <div class="upload-text">
          <p>Drop files here or <span class="upload-link">click to upload</span></p>
          <p class="upload-hint">{{ uploadHint }}</p>
        </div>
      </div>

      <div v-else class="uploading">
        <el-progress :percentage="uploadProgress" :show-text="true"></el-progress>
        <p>Uploading...</p>
      </div>
    </div>

    <!-- File List -->
    <div v-if="uploadedFiles.length > 0" class="uploaded-files">
      <h4>Uploaded Files:</h4>
      <div v-for="file in uploadedFiles" :key="file.url" class="file-item">
        <img v-if="file.type === 'image'" :src="file.url" alt="Uploaded" class="file-preview" />
        <div class="file-info">
          <span class="file-name">{{ file.name }}</span>
          <span class="file-url">{{ file.url }}</span>
        </div>
        <el-button
          type="danger"
          size="small"
          @click="deleteFile(file)"
          icon="el-icon-delete">
          Delete
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { uploadFile, deleteFile } from '@/api/file'

export default {
  name: 'FileUpload',
  props: {
    uploadType: {
      type: String,
      default: 'image', // 'image', 'avatar', 'document'
      validator: value => ['image', 'avatar', 'document'].includes(value)
    },
    multiple: {
      type: Boolean,
      default: false
    },
    maxSize: {
      type: Number,
      default: 10 * 1024 * 1024 // 10MB
    }
  },
  emits: ['upload-success', 'upload-error'],
  setup(props, { emit }) {
    const fileInput = ref(null)
    const isDragOver = ref(false)
    const uploading = ref(false)
    const uploadProgress = ref(0)
    const uploadedFiles = ref([])

    const acceptedTypes = computed(() => {
      switch (props.uploadType) {
        case 'image':
        case 'avatar':
          return '.jpg,.jpeg,.png,.gif'
        case 'document':
          return '.pdf,.doc,.docx'
        default:
          return '*'
      }
    })

    const uploadHint = computed(() => {
      switch (props.uploadType) {
        case 'image':
          return 'Support JPG, PNG, GIF. Max 10MB'
        case 'avatar':
          return 'Support JPG, PNG. Max 2MB'
        case 'document':
          return 'Support PDF, DOC, DOCX. Max 10MB'
        default:
          return 'Select files to upload'
      }
    })

    const triggerFileInput = () => {
      fileInput.value.click()
    }

    const handleFileSelect = (event) => {
      const files = Array.from(event.target.files)
      uploadFiles(files)
    }

    const handleDrop = (event) => {
      isDragOver.value = false
      const files = Array.from(event.dataTransfer.files)
      uploadFiles(files)
    }

    const uploadFiles = async (files) => {
      if (files.length === 0) return

      // Validate files
      for (const file of files) {
        if (file.size > props.maxSize) {
          ElMessage.error(`File ${file.name} is too large`)
          return
        }
      }

      uploading.value = true
      uploadProgress.value = 0

      try {
        for (let i = 0; i < files.length; i++) {
          const file = files[i]
          const formData = new FormData()
          formData.append('file', file)

          const response = await uploadFile(props.uploadType, formData)

          if (response.success) {
            uploadedFiles.value.push({
              name: file.name,
              url: response.data,
              type: file.type.startsWith('image/') ? 'image' : 'document'
            })

            emit('upload-success', response.data)
            ElMessage.success(`${file.name} uploaded successfully`)
          } else {
            throw new Error(response.message)
          }

          uploadProgress.value = Math.round(((i + 1) / files.length) * 100)
        }
      } catch (error) {
        ElMessage.error(`Upload failed: ${error.message}`)
        emit('upload-error', error)
      } finally {
        uploading.value = false
        uploadProgress.value = 0
      }
    }

    const deleteFileHandler = async (file) => {
      try {
        await ElMessageBox.confirm('Are you sure to delete this file?', 'Confirm', {
          type: 'warning'
        })

        await deleteFile(file.url)

        uploadedFiles.value = uploadedFiles.value.filter(f => f.url !== file.url)
        ElMessage.success('File deleted successfully')

      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(`Delete failed: ${error.message}`)
        }
      }
    }

    return {
      fileInput,
      isDragOver,
      uploading,
      uploadProgress,
      uploadedFiles,
      acceptedTypes,
      uploadHint,
      triggerFileInput,
      handleFileSelect,
      handleDrop,
      deleteFile: deleteFileHandler
    }
  }
}
</script>

<style scoped>
.file-upload {
  width: 100%;
}

.upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #f9fafb;
}

.upload-area:hover,
.upload-area.drag-over {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.upload-content i {
  font-size: 48px;
  color: #9ca3af;
  margin-bottom: 16px;
}

.upload-text p {
  margin: 8px 0;
  color: #6b7280;
}

.upload-link {
  color: #3b82f6;
  font-weight: 500;
}

.upload-hint {
  font-size: 12px;
  color: #9ca3af !important;
}

.uploading {
  padding: 20px;
}

.uploaded-files {
  margin-top: 20px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  margin-bottom: 8px;
}

.file-preview {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.file-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.file-url {
  font-size: 12px;
  color: #6b7280;
  word-break: break-all;
}
</style>
```

### Step 2: API Service

Create `src/api/file.js`:

```javascript
import request from '@/utils/request'

/**
 * Upload file
 */
export function uploadFile(type, formData) {
  return request({
    url: `/admin/file/upload/${type}`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * Delete file
 */
export function deleteFile(fileUrl) {
  return request({
    url: '/admin/file/delete',
    method: 'delete',
    params: {
      fileUrl
    }
  })
}
```

---

## 🌐 CDN Setup with CloudFront

### Step 1: Create CloudFront Distribution

1. **Go to CloudFront Console**
2. **Click "Create Distribution"**
3. **Configure Distribution:**

```yaml
Origin Domain: weblog-storage-[your-unique-id].s3.amazonaws.com
Origin Path: /images (optional, for images only)
Name: weblog-s3-origin

Default Cache Behavior:
  Viewer Protocol Policy: Redirect HTTP to HTTPS
  Allowed HTTP Methods: GET, HEAD, OPTIONS, PUT, POST, PATCH, DELETE
  Cache Policy: CachingOptimized
  Origin Request Policy: CORS-S3Origin

Settings:
  Price Class: Use Only North America and Europe
  Alternate Domain Names: cdn.your-domain.com (optional)
  SSL Certificate: Default CloudFront Certificate
```

### Step 2: Configure Custom Domain (Optional)

If you want `cdn.yourdomain.com`:

1. **Request SSL Certificate in ACM** (us-east-1 region)
2. **Add CNAME record in your DNS:**
   ```
   cdn.yourdomain.com → d1234567890.cloudfront.net
   ```
3. **Update CloudFront distribution with custom domain**

### Step 3: Update Application Configuration

Update your Spring Boot configuration:

```yaml
weblog:
  file-upload:
    image-base-url: https://d1234567890.cloudfront.net
    # Or: https://cdn.yourdomain.com
```

---

## 🔒 Security Best Practices

### Step 1: Implement Signed URLs for Private Content

Add to your S3 service:

```java
/**
 * Generate presigned URL for private content
 */
public String generatePresignedUrl(String s3Key, int expirationMinutes) {
    try {
        PresignRequest presignRequest = PresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignedRequest = GetObjectPresignRequest.builder()
                .signRequest(presignRequest)
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignedRequest).url().toString();

    } catch (Exception e) {
        throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage(), e);
    }
}
```

### Step 2: Implement Content Validation

```java
/**
 * Validate uploaded image
 */
public boolean isValidImage(MultipartFile file) {
    try {
        // Check file signature (magic numbers)
        byte[] fileHeader = new byte[8];
        file.getInputStream().read(fileHeader);

        // JPEG: FF D8 FF
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        // GIF: 47 49 46 38

        String hex = bytesToHex(fileHeader);
        return hex.startsWith("FFD8FF") ||  // JPEG
               hex.startsWith("89504E47") || // PNG
               hex.startsWith("47494638");   // GIF

    } catch (Exception e) {
        return false;
    }
}

private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02X", b));
    }
    return sb.toString();
}
```

### Step 3: Implement Rate Limiting

```java
@Component
public class UploadRateLimiter {

    private final Map<String, List<Long>> uploadTimes = new ConcurrentHashMap<>();
    private final int maxUploadsPerHour = 100;

    public boolean isAllowed(String clientId) {
        long now = System.currentTimeMillis();
        long hourAgo = now - TimeUnit.HOURS.toMillis(1);

        uploadTimes.computeIfAbsent(clientId, k -> new ArrayList<>());
        List<Long> times = uploadTimes.get(clientId);

        // Remove old entries
        times.removeIf(time -> time < hourAgo);

        if (times.size() >= maxUploadsPerHour) {
            return false;
        }

        times.add(now);
        return true;
    }
}
```

---

## 📊 Monitoring & Logging

### Step 1: Enable S3 Access Logging

1. **Go to your S3 bucket → Properties**
2. **Scroll to Server access logging**
3. **Click Edit**
4. **Enable logging:**

```yaml
Target bucket: weblog-storage-[your-unique-id]
Target prefix: logs/access/
```

### Step 2: Enable CloudTrail (Optional)

1. **Go to CloudTrail Console**
2. **Create Trail**
3. **Configure S3 data events:**

```yaml
Trail name: weblog-s3-trail
S3 bucket: weblog-storage-[your-unique-id]
Data events: Read and Write
```

### Step 3: Add Application Metrics

```java
@Component
public class S3Metrics {

    private final MeterRegistry meterRegistry;
    private final Counter uploadCounter;
    private final Counter downloadCounter;
    private final Timer uploadTimer;

    public S3Metrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.uploadCounter = Counter.builder("s3.uploads.total")
                .description("Total number of S3 uploads")
                .register(meterRegistry);
        this.downloadCounter = Counter.builder("s3.downloads.total")
                .description("Total number of S3 downloads")
                .register(meterRegistry);
        this.uploadTimer = Timer.builder("s3.upload.duration")
                .description("S3 upload duration")
                .register(meterRegistry);
    }

    public void incrementUpload() {
        uploadCounter.increment();
    }

    public void incrementDownload() {
        downloadCounter.increment();
    }

    public Timer.Sample startUploadTimer() {
        return Timer.start(meterRegistry);
    }
}
```

---

## 💰 Cost Optimization

### Step 1: Implement Lifecycle Rules

1. **Go to S3 bucket → Management**
2. **Create lifecycle rule:**

```yaml
Rule name: weblog-lifecycle

Rule scope: Limit the scope of this rule using one or more filters
Prefix: logs/

Lifecycle rule actions:
☑️ Transition current versions of objects between storage classes
☑️ Transition previous versions of objects between storage classes
☑️ Delete previous versions of objects
☑️ Delete incomplete multipart uploads

Transitions:
- Standard to Standard-IA: 30 days
- Standard-IA to Glacier: 60 days
- Glacier to Deep Archive: 180 days

Delete previous versions: 90 days
Delete incomplete uploads: 7 days
```

### Step 2: Monitor Costs

Create billing alert:

1. **Go to CloudWatch → Billing**
2. **Create Alarm:**

```yaml
Metric: EstimatedCharges
Currency: USD
Threshold: $10 (or your budget)
Actions: Send email notification
```

### Step 3: Optimize Storage Classes

For different content types:

```java
/**
 * Upload with appropriate storage class
 */
public String uploadWithStorageClass(MultipartFile file, String folder, StorageClass storageClass) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .contentType(file.getContentType())
            .storageClass(storageClass)  // STANDARD, STANDARD_IA, GLACIER, etc.
            .build();

    // ... rest of upload logic
}
```

---

## 🚨 Troubleshooting

### Common Issues & Solutions

#### 1. **CORS Errors**

```bash
# Symptoms: Browser blocks requests from frontend
# Solution: Update CORS configuration in S3 bucket
# Check: Browser developer tools → Network tab
```

#### 2. **Access Denied Errors**

```bash
# Check IAM permissions
aws sts get-caller-identity
aws s3 ls s3://weblog-storage-[your-unique-id]/

# Verify bucket policy
aws s3api get-bucket-policy --bucket weblog-storage-[your-unique-id]
```

#### 3. **Upload Fails**

```bash
# Check file size limits
# Verify content type validation
# Check S3 bucket exists
# Verify network connectivity
```

#### 4. **High Costs**

```bash
# Check storage class distribution
aws s3api list-objects-v2 --bucket weblog-storage-[your-unique-id] --query 'Contents[?StorageClass != `STANDARD`]'

# Monitor data transfer
# Review CloudFront usage
# Check lifecycle rules
```

### Useful AWS CLI Commands

```bash
# List bucket contents
aws s3 ls s3://weblog-storage-[your-unique-id]/ --recursive

# Check bucket size
aws s3 ls s3://weblog-storage-[your-unique-id]/ --recursive --human-readable --summarize

# Sync local folder to S3
aws s3 sync ./local-folder s3://weblog-storage-[your-unique-id]/backup/

# Set bucket versioning
aws s3api put-bucket-versioning --bucket weblog-storage-[your-unique-id] --versioning-configuration Status=Enabled

# Create presigned URL
aws s3 presign s3://weblog-storage-[your-unique-id]/path/to/file.jpg --expires-in 3600
```

---

## ✅ S3 Setup Verification Checklist

After completing the setup, verify everything works:

```bash
# 1. Bucket accessibility
curl -I https://weblog-storage-[your-unique-id].s3.amazonaws.com/

# 2. Public file access
curl -I https://weblog-storage-[your-unique-id].s3.amazonaws.com/images/test-image.jpg

# 3. API endpoints
curl -X POST http://your-ec2-ip:8080/admin/file/upload/image \
  -H "Content-Type: multipart/form-data" \
  -F "file=@test-image.jpg"

# 4. Frontend upload
# Test file upload through your Vue.js application

# 5. CloudFront (if configured)
curl -I https://d1234567890.cloudfront.net/images/test-image.jpg
```

### Application Integration Test

```java
@Test
public void testS3FileUpload() {
    // Create mock file
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.jpg",
        "image/jpeg",
        "test content".getBytes()
    );

    // Test upload
    String fileUrl = s3FileService.uploadArticleImage(file);
    assertNotNull(fileUrl);
    assertTrue(fileUrl.contains("amazonaws.com"));

    // Test file exists
    String s3Key = fileUrl.replace(s3FileService.getImageBaseUrl() + "/", "");
    assertTrue(s3FileService.fileExists(s3Key));

    // Test delete
    s3FileService.deleteFile(fileUrl);
    assertFalse(s3FileService.fileExists(s3Key));
}
```

---

## 🎉 S3 Setup Complete!

Your AWS S3 bucket is now fully configured for your weblog project!

### **What You've Achieved:**
✅ **Scalable Storage**: Unlimited file storage capacity
✅ **Global CDN**: Fast content delivery worldwide
✅ **Security**: Proper IAM permissions and access controls
✅ **Cost Optimization**: Lifecycle rules and storage classes
✅ **Integration**: Complete Spring Boot and Vue.js integration
✅ **Monitoring**: Access logging and metrics
✅ **Backup Strategy**: Versioning and lifecycle management

### **Next Steps:**
1. **Test file uploads** through your application
2. **Monitor costs** and adjust as needed
3. **Set up automated backups** for critical data
4. **Configure custom domain** for CDN (optional)
5. **Implement image optimization** (resizing, compression)

Your weblog now has enterprise-grade file storage! 🚀