# Dockerfile — Session 60: Package application for deployment
# Build:  docker build -t gitlab-cicd-demo .
# Run:    docker run -p 5000:5000 gitlab-cicd-demo

# ── Stage 1: Build / dependency install ──────────────────────────────────────
FROM python:3.11-slim AS builder

WORKDIR /build

# Copy dependency list first (Docker layer caching: only reinstalls on changes)
COPY requirements.txt .

RUN pip install --upgrade pip \
 && pip install --no-cache-dir -r requirements.txt

# ── Stage 2: Lean runtime image ───────────────────────────────────────────────
FROM python:3.11-slim AS runtime

# Security: run as non-root user
RUN useradd --create-home appuser
USER appuser
WORKDIR /home/appuser/app

# Copy installed packages from builder stage
COPY --from=builder /usr/local/lib/python3.11/site-packages \
                    /usr/local/lib/python3.11/site-packages

# Copy application source
COPY app.py .

# Expose Flask default port
EXPOSE 5000

# Environment variables (can be overridden at runtime / via CI variables)
ENV FLASK_ENV=production \
    FLASK_APP=app.py \
    PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

# Health-check — GitLab environments can poll this
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
  CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:5000/health')"

CMD ["python", "-m", "flask", "run", "--host=0.0.0.0", "--port=5000"]
