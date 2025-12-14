# Project Progress Summary

**Date:** December 10, 2025
**Status:** All requested features implemented and deployed.

## 1. Infrastructure & Deployment Fixes

### Docker & Server
*   **Backend Image:** Updated `docker/docker-compose.yml` to use `eclipse-temurin:11-jdk` instead of the deprecated `openjdk:11`.
*   **MinIO Configuration:** Fixed image upload failures by updating `MINIO_PUBLIC_URL` in `docker/.env` to use the correct AWS IP (`98.82.37.203`) instead of the placeholder.
*   **Docker Compose:** Resolved `KeyError: 'ContainerConfig'` by performing a full `docker-compose down` and `up -d` reset.

### Jenkins CI/CD
*   **Pipeline Error:** Fixed `maven:3.8-openjdk-8` entrypoint issue in `Jenkinsfile` by adding `--entrypoint=''`.
*   **Backend Tests:** Fixed H2 database test failures by:
    *   Forcing H2 usage via `@TestPropertySource` in `WeblogWebApplicationTests.java`.
    *   Updating `src/test/resources/schema.sql` to include the missing `type` column in `t_article`.
*   **Frontend Linting:** Fixed multiple ESLint errors in `.vue` files (indentation, unused variables, duplicate imports).

---

## 2. Backend Implementation (Wiki Module)

### Database
*   **New Tables:** Created `t_wiki` and `t_wiki_catalog` tables (Migration script: `weblog/sql/migration_wiki.sql`).
*   **Schema Update:** Added `type` column to `t_article` to distinguish between Normal Articles (`1`) and Wiki Articles (`2`).

### Codebase (`weblog-module-admin`, `weblog-module-common`)
*   **Entity Classes:** Created `WikiDO`, `WikiCatalogDO`.
*   **Mappers:** Created `WikiMapper`, `WikiCatalogMapper`. Updated `ArticleMapper` to support filtering by `type`.
*   **Service Layer:**
    *   Implemented `AdminWikiService` interface and `AdminWikiServiceImpl`.
    *   **Features:** Add, Delete, Update, Page List, Update IsTop/IsPublish.
    *   **Catalog Logic:** Implemented complex logic to update the entire catalog tree (Level 1 & 2), including batch inserts and updating associated article types.
*   **Controller:** Created `AdminWikiController` with endpoints for all Wiki operations.

---

## 3. Frontend Implementation (Wiki Module)

### Pages & Components
*   **Wiki List Page (`wiki-list.vue`):**
    *   Implemented Paginated List, Fuzzy Search, Date Filtering.
    *   Added "New Wiki" and "Edit Wiki" Dialogs (Title, Cover, Summary).
    *   Implemented Toggle Switches for "Is Top" and "Is Publish".
    *   Implemented "Delete" functionality.
*   **Catalog Editor (`WikiCatalogEditDialog.vue`):**
    *   **Structure:** Implemented a recursive Accordion UI for Level 1 and Level 2 catalogs.
    *   **Drag & Drop:** Integrated `vue-draggable-plus` to allow sorting of Level 2 catalog items.
    *   **CRUD:** Implemented adding, renaming, and deleting catalogs (Level 1 & 2).
    *   **Article Integration:** Implemented "Add Article" dialog with search and selection to add existing articles to the wiki structure.
    *   **Real-time Saving:** Configured actions (Move, Rename, Delete) to trigger auto-save API calls.

### API & Routing
*   **API Service:** Created `src/api/admin/wiki.js` encapsulating all backend endpoints.
*   **Router:** Added `/admin/wiki/list` route in `router/index.js`.
*   **Menu:** Added "Wiki Management" to `AdminMenu.vue`.

---

## 4. Current State

*   **Backend:** Running version `0.0.1-SNAPSHOT`. Health check passed.
*   **Frontend:** Deployed to Nginx. Wiki Management is accessible in the Admin Panel.
*   **Data:** Wiki tables created in MySQL.
*   **Storage:** MinIO bucket `weblog` configured and public.

## 5. Next Steps (Optional)

*   **Public Wiki View:** Implement the public-facing pages to display Wikis to visitors (e.g., `/wiki/list` and `/wiki/{id}`).
*   **Optimization:** Review slow SQL queries if dataset grows large.
