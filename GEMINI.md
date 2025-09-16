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
