# Hyperlofy

Hyperlofy is a full-stack marketplace and delivery workflow project with:
- `app/` — Android client built with Jetpack Compose and Room local persistence.
- `backend/` — Spring Boot backend with JWT authentication, PostgreSQL, Redis, and payment integration.
- `docker-compose.yml` — local development stack for backend dependencies.

## Repository structure

- `app/` — Android application module
- `backend/` — Spring Boot backend module
- `gradle/` — dependency and wrapper configuration
- `.env.example` — example environment variables for local development

## Prerequisites

- Android Studio
- JDK 21
- Gradle Wrapper (`./gradlew` / `gradlew.bat`)
- Docker and Docker Compose for backend services
- Git installed for version control

## Local setup

1. Copy `.env.example` to `.env` and update values for your environment.
2. Open the project root in Android Studio.
3. Sync Gradle and allow Android Studio to resolve dependencies.
4. Build the Android app with:
   - `./gradlew clean assembleDebug`
5. Run the backend services with Docker Compose:
   - `docker-compose up --build`
6. Build and run the backend service if needed:
   - `./gradlew :backend:bootJar`

## Notes

- Do not commit `.env` or any local secret files.
- `.gitignore` is configured to exclude IDE files, build outputs, and local configuration.
- The repository currently does not contain a Git history in this environment because Git is not installed.
