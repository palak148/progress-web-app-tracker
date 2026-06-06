# Deploy Prep Tracker in ~10 minutes (no Docker needed on your Mac)

## Option A — Render.com (recommended, free)

### 1. Push code to GitHub

```bash
cd /Users/mindstix/Project
git init
git add -A
git commit -m "Prep Tracker ready for deploy"
```

Create a new repo at https://github.com/new named `prep-tracker`, then:

```bash
git remote add origin git@github.com:YOUR_USERNAME/prep-tracker.git
git branch -M main
git push -u origin main
```

Or run the helper script:

```bash
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

### 2. Deploy on Render

1. Sign up at **[render.com](https://render.com)** (free)
2. Click **New → Blueprint**
3. Connect GitHub → select **prep-tracker**
4. Render auto-reads `render.yaml` and creates:
   - PostgreSQL database
   - Backend API (Spring Boot)
   - Frontend (React static site)
5. Click **Apply** — wait ~10 minutes for first build

### 3. Use your app

After deploy you'll get URLs like:

- **App:** `https://prep-tracker-web.onrender.com`
- **API:** `https://prep-tracker-api.onrender.com`

Open the **web** URL on phone + laptop → Register → Add to Home Screen.

### Free tier notes

- App **sleeps after 15 min** of no use — first open takes ~30 sec to wake up
- For always-on, upgrade Render to $7/mo or use a VPS (Option B)

### Google login on production

In Google Cloud Console, add redirect URI:

```
https://prep-tracker-api.onrender.com/login/oauth2/code/google
```

In Render dashboard → **prep-tracker-api** → Environment:

- `GOOGLE_CLIENT_ID` = your client id
- `GOOGLE_CLIENT_SECRET` = your secret

---

## Option B — VPS with Docker ($4–6/month, always on)

Best if you want no sleep delays.

1. Rent a VPS (Hetzner, DigitalOcean, Oracle free tier)
2. Install Docker on the server
3. Copy project to server
4. Run:

```bash
cp .env.example .env
nano .env   # set APP_URL, DB_PASSWORD, JWT_SECRET
docker compose up -d --build
```

See [DEPLOY.md](DEPLOY.md) for full VPS guide.

---

## Option C — Docker on your Mac (local network only)

Install [Docker Desktop](https://www.docker.com/products/docker-desktop/), then:

```bash
cd /Users/mindstix/Project
cp .env.example .env
docker compose up -d --build
```

Open http://localhost on your Mac. Phone on same Wi‑Fi: `http://YOUR_MAC_IP`.

Not accessible outside your home network unless you port-forward.

---

## Quick comparison

| Option | Cost | Phone anywhere | Always on |
|--------|------|----------------|-----------|
| **Render (A)** | Free | ✅ | ❌ (sleeps) |
| **VPS (B)** | ~$5/mo | ✅ | ✅ |
| **Local Docker (C)** | Free | Same Wi‑Fi only | ✅ |

**Recommendation:** Start with **Render (Option A)** today. Upgrade to VPS later if you want faster always-on access.
