# Prep Tracker

Track exam preparation progress (RBI Grade B or any exam) with date-based tasks, streaks, and a LeetCode-style activity heatmap. Syncs across phone and laptop via a Spring Boot backend.

## Stack

- **Backend:** Spring Boot 2.7, Spring Security, JWT, Google OAuth2, JPA, H2 (dev) / PostgreSQL (prod)
- **Frontend:** React, TypeScript, Vite, Tailwind CSS, PWA

## Features

- **Multiple projects** — create folders like "RBI Grade B", "UPSC", each with its own tasks, streak & heatmap
- **Daily tasks** — plan by date, mark complete
- **Streaks** — LeetCode-style current/longest streak per project
- **Activity heatmap** — year view of completed days
- **Sync** — login from phone + laptop, same data everywhere
- **Auth** — email/password + Google OAuth

## Project structure

```
Project/
├── backend/          # Spring Boot REST API
├── frontend/         # React PWA
├── docker-compose.yml
└── DEPLOY.md         # Server deployment guide
```

## Prerequisites

- Java 11+
- Maven 3.6+
- Node.js 14.18+ (18+ recommended)

## Quick start

### 1. Backend

```bash
cd backend
mvn spring-boot:run
```

API runs at `http://localhost:8080`

Uses embedded H2 database by default (file stored in `backend/data/`).

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

App runs at `http://localhost:5173` (proxies API calls to backend).

### 3. Register & use

1. Open `http://localhost:5173`
2. Register with email/password, or use Google (see below)
3. Add daily tasks, mark complete, watch your streak grow

## Deploy to a server (daily use)

See **[DEPLOY.md](DEPLOY.md)** for full instructions. Quick version:

```bash
cp .env.example .env   # edit with your server IP & secrets
docker compose up -d --build
```

Open `http://YOUR_SERVER_IP` on phone and laptop. Add to Home Screen for app-like use.

## Google OAuth setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create OAuth 2.0 credentials (Web application)
3. Authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
4. Set environment variables before starting backend:

```bash
export GOOGLE_CLIENT_ID=your-client-id
export GOOGLE_CLIENT_SECRET=your-client-secret
export FRONTEND_URL=http://localhost:5173
export JWT_SECRET=your-long-random-secret-at-least-32-characters
```

## PostgreSQL (production)

```bash
export SPRING_PROFILES_ACTIVE=postgres
export DATABASE_URL=jdbc:postgresql://localhost:5432/prep_tracker
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
cd backend && mvn spring-boot:run
```

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register` | Email registration |
| POST | `/api/auth/login` | Email login |
| GET | `/api/auth/me` | Current user |
| GET | `/oauth2/authorization/google` | Google sign-in |
| GET/POST | `/api/plans` | Prep plans |
| GET/POST | `/api/tasks` | Tasks by date |
| PATCH | `/api/tasks/{id}/complete` | Mark done |
| GET | `/api/stats/streak` | Streak & month stats |
| GET | `/api/stats/heatmap?year=2026` | Year activity map |

## Streak rules

- Complete at least **one task** on a calendar day to count as an active day
- **Current streak:** consecutive active days up to today (or yesterday if today not done yet)
- **Longest streak:** best run of consecutive active days ever

## PWA install

In Chrome (mobile or desktop): open the app → menu → **Install app** / **Add to Home Screen**.
