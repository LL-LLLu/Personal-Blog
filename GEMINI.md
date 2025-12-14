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

## Recent Actions & Implementation Log

This section details the significant changes and implementations performed to add the **Wiki Module** and resolve deployment issues.

### 1. Wiki Module Implementation

#### **Backend (Spring Boot)**
*   **Database Schema:** Created `t_wiki` and `t_wiki_catalog` tables. Updated `t_article` to include a `type` column (1: Normal, 2: Wiki).
*   **API Implementation:**
    *   Created `AdminWikiController` with endpoints for CRUD, Top, Publish, and Catalog management.
    *   Created `AdminWikiService` and `AdminWikiServiceImpl` implementing the logic.
    *   Created VOs: `AddWikiReqVO`, `UpdateWikiReqVO`, `FindWikiPageListReqVO`, `FindWikiCatalogListReqVO`, `UpdateWikiCatalogReqVO`.
    *   Updated `ArticleMapper` to support filtering by `type` in `selectPageList`.
    *   Updated `AdminArticleServiceImpl` to pass `type` parameter for filtering.
    *   Updated `ArticleServiceImpl` and `ArchiveServiceImpl` (Public API) to ignore `type` (pass `null`) to maintain existing behavior.

#### **Frontend (Vue 3)**
*   **Wiki List Page:** Created `src/pages/admin/wiki-list.vue` with search, pagination, and action buttons (Edit, Edit Catalog, Delete, Top, Publish).
*   **Routing:** Added `/admin/wiki/list` route in `src/router/index.js`.
*   **Menu:** Added "Wiki Management" to `src/layouts/admin/components/AdminMenu.vue`.
*   **API Client:** Created `src/api/admin/wiki.js` encapsulating all backend Wiki endpoints.
*   **Catalog Editor (`WikiCatalogEditDialog.vue`):**
    *   Implemented a complex dialog for managing the Wiki structure.
    *   **Features:**
        *   Add/Remove Level 1 Catalogs.
        *   Add/Remove Level 2 Catalogs (Articles).
        *   Rename catalogs inline (using `el-input` toggle).
        *   **Drag-and-Drop Sorting:** Integrated `vue-draggable-plus` to reorder Level 2 items.
        *   **Manual Sorting:** Implemented "Move Up" / "Move Down" for Level 1 items.
        *   **Article Selection:** Integrated a nested `FormDialog` to search and select existing articles to add to the Wiki.
    *   **Real Data Integration:** Connected to `getWikiCatalogs` and `updateWikiCatalogs` APIs for loading and saving changes.

#### **Wiki Catalog Implementation Details**
*   **Database:** New table `t_wiki_catalog` stores the hierarchical structure (Level 1: Chapter, Level 2: Article).
*   **Backend Logic:**
    *   **Full Sync Approach:** The `updateWikiCatalogs` method simplifies reordering by deleting all existing catalogs for a wiki and re-inserting the entire tree structure provided by the frontend.
    *   **Article Type Management:** Automatically manages the `type` field of articles. When an article is added to a catalog, its type updates to `2` (Wiki). If removed (or the catalog is deleted), it reverts to `1` (Normal).
*   **Frontend UI (`WikiCatalogEditDialog.vue`):**
    *   **Tree Structure:** Visualized using a custom Accordion-style list with Tailwind CSS.
    *   **Inline Editing:** Titles for both Level 1 and Level 2 items can be renamed directly in the list (click "Rename" -> input toggles).
    *   **Drag & Drop:** Integrated `vue-draggable-plus` to allow dragging Level 2 articles to reorder them within a chapter.
    *   **Manual Sorting:** Added "Move Up" / "Move Down" buttons for Level 1 chapters, conditionally rendered based on list position.
    *   **Article Selector:** A nested search dialog allows finding existing "Normal" articles and converting them into Wiki articles by adding them to the catalog.
    *   **Auto-Save:** Actions like moving, renaming, or deleting trigger an immediate save (`updateWikiCatalogsData`) to the backend to ensure data consistency.

### 2. Deployment & Troubleshooting

#### **Jenkins Pipeline**
*   **Docker Agent Issue:** Fixed `Jenkinsfile` to use `args '-v /root/.m2:/root/.m2 --entrypoint=""'` for the Maven container to prevent entrypoint conflicts.
*   **Test Failures:**
    *   Fixed Backend Tests by adding the missing `type` column to the H2 test schema `schema.sql`.
    *   Fixed Frontend Lint errors (duplicate imports, SVG parsing issues) in `WikiCatalogEditDialog.vue`.

#### **Production Environment (AWS)**
*   **502 Bad Gateway:** Diagnosed and fixed backend crash caused by missing `type` column and `t_wiki` tables in the production MySQL database.
*   **Database Migration:** Manually executed SQL migrations (`ALTER TABLE` and `CREATE TABLE`) on the running MySQL container.
*   **MinIO Uploads:** Fixed image upload failure where the backend returned an internal IP (`1.2.3.4`). Updated `.env` with the correct AWS Public IP and forced container recreation.
*   **Docker Compose:** Resolved `KeyError: 'ContainerConfig'` issues with old `docker-compose` version by performing a full `docker-compose down && docker-compose up -d`.

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