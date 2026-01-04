# MCH Hub

An application for managing and viewing [mch](https://github.com/Alvinn8/mch) repositories.

## Stack
- Frontend: Vue, Vite, PrimeVue, Vue Router
- Backend: Spring Boot
- Database: PostgreSQL
- File Manager: [Alvinn8/ftp-client](https://github.com/Alvinn8/ftp-client)

## Quick start (all-in-docker)
```sh
docker-compose up --build
```
The application is available at http://localhost/

## Local development
Run the **frontend and backend on your host**, but keep **Postgres + file-manager (frontend+backend)** in Docker.

### 1) Start Docker-only dependencies
Use the dedicated dev compose file:

```sh
docker compose -f docker-compose.dev.yml up --build
```

This starts:
- Postgres on `localhost:5432`
- File-manager backend on `localhost:8081`
- File-manager frontend on `localhost:9000`

> macOS/Windows: `host.docker.internal` works by default.

### 2) Run the backend on your host
From `backend/`:

```sh
./gradlew bootRun
```

Backend runs on `http://localhost:8080` and connects to Postgres on `localhost:5432`.

### 3) Run the frontend on your host
From `frontend/`:

```sh
pnpm install
pnpm dev
```

Frontend runs on `http://localhost:5173`.

Vite proxies `/api/*` to the backend (default `http://localhost:8080`), so the browser doesn’t need CORS for API calls.
