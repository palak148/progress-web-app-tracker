# Deploy Prep Tracker to a VPS

Run Prep Tracker on any Linux server (DigitalOcean, AWS EC2, Hetzner, etc.) with Docker.

## What you need

- A VPS with **2 GB RAM** (1 GB works for light use)
- Ubuntu 22.04+ recommended
- A domain name (optional but recommended for HTTPS)

## Step 1 — Install Docker on the server

SSH into your server, then:

```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
# Log out and back in, then:
docker compose version
```

## Step 2 — Upload the project

**Option A — Git (recommended)**

```bash
git clone YOUR_REPO_URL prep-tracker
cd prep-tracker
```

**Option B — Copy from your laptop**

```bash
scp -r /Users/mindstix/Project user@YOUR_SERVER_IP:~/prep-tracker
ssh user@YOUR_SERVER_IP
cd ~/prep-tracker
```

## Step 3 — Configure environment

```bash
cp .env.example .env
nano .env
```

Set at minimum:

```env
APP_URL=http://YOUR_SERVER_IP
APP_PORT=80
DB_PASSWORD=your-strong-db-password
JWT_SECRET=your-long-random-jwt-secret-min-32-chars
```

If you have a domain:

```env
APP_URL=https://prep.yourdomain.com
```

## Step 4 — Start the app

```bash
docker compose up -d --build
```

Open **http://YOUR_SERVER_IP** in your browser (phone + laptop).

Check logs if needed:

```bash
docker compose logs -f backend
docker compose logs -f frontend
```

## Step 5 — HTTPS (recommended for daily use)

Install Caddy as a reverse proxy with automatic SSL:

```bash
sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt update && sudo apt install caddy
```

Create `/etc/caddy/Caddyfile`:

```
prep.yourdomain.com {
    reverse_proxy localhost:80
}
```

```bash
sudo systemctl reload caddy
```

Update `.env`:

```env
APP_URL=https://prep.yourdomain.com
CORS_ORIGINS=https://prep.yourdomain.com
```

Then restart:

```bash
docker compose up -d
```

## Google sign-in on production

In Google Cloud Console, add authorized redirect URI:

```
https://prep.yourdomain.com/login/oauth2/code/google
```

Set `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` in `.env`, then:

```bash
docker compose up -d
```

## Useful commands

```bash
# Stop
docker compose down

# Restart after code changes
docker compose up -d --build

# View running containers
docker compose ps

# Backup database
docker compose exec db pg_dump -U prep prep_tracker > backup.sql
```

## Cheapest hosting options

| Provider | Cost | Notes |
|----------|------|-------|
| **Hetzner** | ~€4/mo | Good value in EU |
| **DigitalOcean** | $6/mo | Easy droplets |
| **Oracle Cloud** | Free tier | Always-free ARM VM available |
| **Railway / Render** | Free tier limited | Easier but less control |

For a personal daily tracker, a **$4–6/month VPS + Docker** is the best balance of cost and control.

## Install on phone

After deploying, open your URL in Chrome → **Add to Home Screen**. Works like an app with sync across devices.
