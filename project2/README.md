# Full-Stack Mobile Interview Project

This project consists of a **Node.js Backend API** and a **native Android Client**. The application allows users to view and manage a list of tasks, with support for offline access via a local SQLite database.

## Prerequisites

*   **Docker & Docker Compose** (for the backend)
*   **Android Studio** Flamingo or newer
*   **Java JDK 11+**
*   **Android Emulator** (API Level 30+ recommended)

## Setup & Execution Guide

Follow the steps below to set up the environment and run the application.

### 1. Backend Service Setup

The backend serves the API endpoints required by the mobile application. It is containerized for consistent execution.

1.  Open a terminal in the root of this project.
2.  Start the backend service using Docker Compose:
    ```bash
    docker-compose up --build
    ```
3.  **Validation Goal:** Ensure the container starts successfully and the API is accessible at `http://localhost:3000/api/tasks` (or the configured port).

### 2. Android Client Setup

The Android application connects to the backend to fetch tasks and caches them locally.

1.  Open Android Studio.
2.  Select **"Open an existing Android Studio project"** and navigate to the `android-client` directory.
3.  Allow the Gradle project to sync and build.
4.  Select a configured Android Emulator (ensure it has internet access).
5.  Click **Run** (green play button).

### 3. Verification & Task

Your objective is to verify that the system functions as a production-ready application.

**Success Criteria:**
1.  **Startup:** The application launches on the emulator causing no crashes.
2.  **Data Loading:** The app successfully connects to the local backend API and fetches the list of tasks.
3.  **Display:** Tasks are correctly rendered in the list view.
4.  **Persistence:** Tasks are saved to the local database. Restarting the app with no network should display cached data.
5.  **Stability:** The app handles screen rotation and network variances without crashing or losing data.

**Instructions:**
If the application fails to build, crash on launch, fails to load data, or behaves unexpectedly during any of the above steps, investigate the root cause in the codebase and apply the necessary fixes to achieve the success criteria.
