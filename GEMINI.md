# GEMINI Project Context

This document provides a comprehensive overview of the projects in this directory to be used as instructional context for future interactions.

## Project Overview

This directory contains a full-stack web application consisting of a Vue.js frontend and a Spring Boot backend.

*   **`blog-vue3`**: A modern frontend application built with Vue 3 and Vite. It uses various libraries, including Vue Router for navigation, Pinia for state management, and Element Plus for UI components.
*   **`weblog/weblog-springboot`**: A robust backend application built with Spring Boot. It's a multi-module Maven project that provides a RESTful API for the frontend. Key technologies include Spring Security for authentication, JWT for authorization, MyBatis-Plus for database interaction, and Lucene for full-text search.

## Building and Running

### `blog-vue3` (Frontend)

**1. Install Dependencies:**

```bash
npm install
```

**2. Run in Development Mode:**

```bash
npm run dev
```

**3. Build for Production:**

```bash
npm run build
```

### `weblog/weblog-springboot` (Backend)

**1. Build the Project:**

This is a Maven project. You can build it using the following command from the `weblog/weblog-springboot` directory:

```bash
mvn clean package
```

**2. Run the Application:**

The main entry point is in the `weblog-web` module. You can run the application using your IDE by running the main class, or by using the following command:

```bash
# TODO: Add the command to run the application from the command line.
# This typically involves running the generated JAR file.
# e.g., java -jar weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar
```

**3. Database and Minio:**

*   The application uses a MySQL database named `weblog`. Make sure you have a running MySQL instance and have created the database.
*   The application uses Minio for object storage. Make sure you have a running Minio instance.
*   Database and Minio connection details can be configured in `weblog/weblog-springboot/weblog-web/src/main/resources/application-dev.yml`.

## Development Conventions

### Frontend

*   The project follows standard Vue.js conventions.
*   Components are located in `src/components`.
*   Pages are located in `src/pages`.
*   State management is handled by Pinia in `src/stores`.

### Backend

*   The project is a multi-module Maven project.
*   `weblog-web`: Contains the main application entry point and web layer.
*   `weblog-module-admin`: Contains the admin-related logic.
*   `weblog-module-common`: Contains common utilities and classes.
*   `weblog-module-jwt`: Contains JWT-related logic.
*   `weblog-module-search`: Contains search-related logic.
*   The application uses JWT for authentication and authorization.
*   The application uses Lucene for full-text search.

---

## Docker Deployment & Troubleshooting Guide

This section outlines common issues encountered during Docker deployment and provides solutions.

### 1. Docker Compose `version` Warning

*   **Issue:** `WARN[0000] ...docker-compose.yml: the attribute 'version' is obsolete, it will be ignored...`
*   **Solution:** This is a warning, not an error. It indicates that the `version` field in `docker-compose.yml` (e.g., `version: '3.8'`) is no longer strictly necessary in recent Docker Compose versions. You can safely remove this line from your `docker-compose.yml` to suppress the warning, but the file will still work as is.

### 2. Backend Image `openjdk:11` Not Found

*   **Issue:** `Error response from daemon: manifest for openjdk:11 not found: manifest unknown: manifest unknown`
*   **Reason:** The `openjdk:11` image tag has been deprecated or removed from Docker Hub.
*   **Solution:** Update your `docker/docker-compose.yml` file to use a supported image, such as `eclipse-temurin:11-jdk`.
    *   **Old:** `image: openjdk:11`
    *   **New:** `image: eclipse-temurin:11-jdk`
    *   You can apply this change using `sed` on your server:
        ```bash
        sed -i 's/image: openjdk:11/image: eclipse-temurin:11-jdk/g' docker-compose.yml
        ```

### 3. Backend `JAR` File (Java Application) Invalid/Corrupt

*   **Issue:** `Error: Invalid or corrupt jarfile weblog-web-0.0.1-SNAPSHOT.jar`
*   **Reason:** This occurs when the `weblog-web-0.0.1-SNAPSHOT.jar` file is either missing or incorrectly created as a directory on the host system where the Docker volume is mounted. This often happens if `docker-compose up` was run before the Maven build completed successfully.
*   **Solution:**
    1.  **Stop all containers:** `docker-compose down`
    2.  **Remove the problematic directory/file:** `sudo rm -rf ../weblog/weblog-springboot/weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar` (Ensure you use `sudo` if permission issues occur).
    3.  **Build the backend JAR using a Dockerized Maven container** (if `mvn` is not installed on the host):
        ```bash
        docker run --rm -v "$(pwd)/../weblog/weblog-springboot":/usr/src/mymaven -w /usr/src/mymaven maven:3.8-openjdk-8 mvn clean package -DskipTests
        ```
    4.  **Verify the JAR file exists and is a file:** `ls -l ../weblog/weblog-springboot/weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar`
    5.  **Restart the backend service:** `docker-compose up -d backend`

### 4. Frontend `dist` Folder Permissions / Nginx Unhealthy

*   **Issue:** `weblog-frontend` container is `(unhealthy)` or Nginx cannot serve files due to permission errors (`EACCES: permission denied`).
*   **Reason:** Similar to the backend JAR, the `blog-vue3/dist` directory (where Vue builds its static assets) might be missing, empty, or have incorrect permissions if `docker-compose up` was run before the frontend build. Nginx's health check will fail if it cannot find `index.html`.
*   **Solution:**
    1.  **Remove the problematic `dist` folder:** `sudo rm -rf ../blog-vue3/dist` (Ensure `sudo` if needed).
    2.  **Build the frontend using a Dockerized Node.js container** (if `npm` is not installed on the host):
        ```bash
        docker run --rm -v "$(pwd)/../blog-vue3":/app -w /app node:16-alpine /bin/sh -c "npm install && npm run build"
        ```
    3.  **Restart the frontend service** (if needed, it might self-correct): `docker-compose restart frontend`

### 5. Port 80 "Address Already in Use" Conflict

*   **Issue:** `failed to bind host port for 0.0.0.0:80:172.18.0.5:80/tcp: address already in use`
*   **Reason:** A system-level service (like Nginx or Apache) is already running on port 80 on your host machine, preventing your Dockerized Nginx (the `weblog-frontend` container) from using it.
*   **Solution:**
    1.  **Identify the conflicting process:** `sudo lsof -i :80`
    2.  **Stop the system service:** `sudo systemctl stop nginx` (or `apache2`).
    3.  **(Optional, but recommended):** Disable the system service to prevent it from starting on reboot: `sudo systemctl disable nginx` (or `apache2`).
    4.  **Start your frontend container:** `docker-compose up -d frontend`

### 6. Accessing the UI / Backend

*   **Web UI (Frontend):** Access your blog directly via the IP address or domain: `http://<YOUR_AWS_PUBLIC_IP>` (This uses port 80).
*   **Backend API:** Your API is available on `http://<YOUR_AWS_PUBLIC_IP>:8080`. Navigating to `http://<YOUR_AWS_PUBLIC_IP>:8080/` might show a "Whitelabel Error Page" if no root mapping exists, which indicates the backend is running successfully. Try specific API endpoints or Swagger documentation if available (e.g., `http://<YOUR_AWS_PUBLIC_IP>:8080/doc.html` or `http://<YOUR_AWS_PUBLIC_IP>:8080/swagger-ui.html`).
*   **Jenkins UI:** `http://<YOUR_AWS_PUBLIC_IP>:8081`

By following these steps, you should be able to get your full stack deployed and running smoothly on your AWS server.