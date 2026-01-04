# MCH Hub

A Vue + Java (Spring Boot) hub for browsing MCH repositories (users, orgs, repos, commits). File viewer is intentionally left as an iframe placeholder; supply your own external file manager via environment.

## Stack
- Frontend: Vue 3, Vite, PrimeVue, Vue Router
- Backend: Spring Boot 3, JPA, PostgreSQL
- Database: PostgreSQL
- Container: Docker Compose

## Quick start
```sh
# from repo root
docker-compose up --build
```
Frontend available at http://localhost:5173 (served by nginx). Backend at http://localhost:8080.

### Environment knobs
- Backend:
  - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
  - `FILE_VIEWER_BASE_URL` used when you add a real file viewer endpoint.
- Frontend build args (set in docker-compose or your shell before `npm run build`):
  - `VITE_API_BASE` (default `/api` in dev proxy)
  - `VITE_FILE_VIEWER_BASE` (iframe src template)

## API outline (read-only)
- `GET /api/users` list users
- `GET /api/users/{username}` user detail
- `GET /api/users/{username}/repos` repos owned by user
- `GET /api/orgs` list orgs
- `GET /api/orgs/{slug}` org detail
- `GET /api/orgs/{slug}/repos` repos owned by org
- `GET /api/repos/users/{username}/{repo}` repo detail (user-owned)
- `GET /api/repos/orgs/{slug}/{repo}` repo detail (org-owned)
- `GET /api/repos/users/{username}/{repo}/commits` commits list
- `GET /api/repos/orgs/{slug}/{repo}/commits` commits list
- `GET /api/repos/users/{username}/{repo}/commits/{hash}` commit detail
- `GET /api/repos/orgs/{slug}/{repo}/commits/{hash}` commit detail

## Notes
- Persistence model is minimal: users, orgs, repos, commits. Ownership allows user or org.
- Integration with actual MCH repository content is left for later (replace placeholders in services/controllers and the iframe src in frontend).
- Demo data auto-seeds on backend start if DB is empty.
- File viewer: frontend embeds `VITE_FILE_VIEWER_BASE?repo={owner/repo}&hash={hash}`; swap URL format to match your external manager.
