#!/bin/bash
# Prep Tracker — push to GitHub and deploy on Render.com
set -e

echo "=== Prep Tracker Deploy ==="
echo ""

if ! command -v git &>/dev/null; then
  echo "Install git first: https://git-scm.com"
  exit 1
fi

cd "$(dirname "$0")/.."

# Init git if needed
if [ ! -d .git ]; then
  git init
  git branch -M main
fi

git add -A
git commit -m "Prep Tracker: ready for cloud deploy" 2>/dev/null || echo "Nothing new to commit"

echo ""
echo "Step 1 — Create a GitHub repo"
echo "  Go to: https://github.com/new"
echo "  Name it: prep-tracker"
echo "  Do NOT add README (repo should be empty)"
echo ""
read -p "Enter your GitHub username: " GH_USER

REMOTE="git@github.com:${GH_USER}/prep-tracker.git"

if git remote | grep -q origin; then
  git remote set-url origin "$REMOTE"
else
  git remote add origin "$REMOTE"
fi

echo ""
echo "Step 2 — Pushing code..."
git push -u origin main

echo ""
echo "Step 3 — Deploy on Render"
echo "  1. Sign up: https://render.com (free)"
echo "  2. Dashboard → New → Blueprint"
echo "  3. Connect your GitHub → select prep-tracker repo"
echo "  4. Render reads render.yaml and creates everything automatically"
echo "  5. Wait ~10 min for first build"
echo ""
echo "Your app will be live at:"
echo "  https://prep-tracker-web.onrender.com  (frontend)"
echo "  https://prep-tracker-api.onrender.com  (API)"
echo ""
echo "Add to phone home screen from Chrome for daily use!"
echo ""
echo "Note: Free tier sleeps after 15 min idle — first load may take ~30 sec."
