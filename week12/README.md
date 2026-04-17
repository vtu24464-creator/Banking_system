# GitLab CI/CD Demo Project
### Sessions 57 – 60 | Introduction → Automated Deployment

---

## Project Structure

```
gitlab-cicd-project/
├── app.py              ← Flask web app (the code we're shipping)
├── test_app.py         ← Unit tests (pytest)
├── requirements.txt    ← Python dependencies
├── Dockerfile          ← Container image for deployment
├── .gitlab-ci.yml      ← THE CI/CD PIPELINE (heart of this project)
└── README.md
```

---

## Session 57 — Introduction to GitLab & CI/CD

### Key Concepts

| Term | Meaning |
|---|---|
| **Pipeline** | The full automated workflow triggered by a git push |
| **Job** | A single unit of work (e.g., run tests) |
| **Stage** | A group of jobs that run together; stages are sequential |
| **Runner** | A server (shared or self-hosted) that executes jobs |

### Quick-Start: Push this project to GitLab

```bash
# 1. Create a new project on GitLab.com (UI → New Project → Blank)
# 2. Clone and add files:
git clone https://gitlab.com/<your-username>/<your-project>.git
cd <your-project>

# Copy all project files here, then:
git add .
git commit -m "feat: initial project setup"
git push origin main
# → Pipeline triggers automatically!
```

---

## Session 58 — GitLab CI + Git Integration

The `.gitlab-ci.yml` file in the repo root is picked up by GitLab **automatically** on every commit.

### Pipeline triggers defined in `.gitlab-ci.yml`

```yaml
rules:
  - if: '$CI_PIPELINE_SOURCE == "push"'          # Every push to any branch
  - if: '$CI_COMMIT_BRANCH == "main"'            # Only on main branch
  - if: '$CI_PIPELINE_SOURCE == "merge_request_event"'  # On MR creation/update
  - if: '$CI_PIPELINE_SOURCE == "schedule"'      # Scheduled (nightly)
```

---

## Session 59 — Pipeline Basics

### Pipeline stages (in order)

```
build ──► test ──► package ──► deploy
```

### Jobs per stage

| Stage | Job | What it does |
|---|---|---|
| `build` | `install_and_lint` | Install deps, run flake8 linter |
| `test` | `unit_tests` | Run pytest, generate JUnit + coverage reports |
| `test` | `mr_safety_check` | Quick check on Merge Requests |
| `package` | `build_docker_image` | Build & push Docker image to registry |
| `deploy` | `deploy_staging` | Auto-deploy to staging (main branch only) |
| `deploy` | `deploy_production` | Manual-approval deploy to production |

### View pipeline graph

GitLab UI → Your Project → **CI/CD → Pipelines** → click a pipeline → graph view.

### View job logs

Click any job in the pipeline graph to see real-time stdout output.

---

## Session 60 — Automated Build, Testing & Deployment

### Running tests locally

```bash
pip install -r requirements.txt
pytest test_app.py -v                          # Verbose output
pytest test_app.py --junitxml=reports/junit.xml  # JUnit report
pytest test_app.py --cov=app --cov-report=html   # HTML coverage report
```

### Artifacts (downloadable from GitLab UI)

After the `unit_tests` job runs:

1. Go to **CI/CD → Pipelines** → click the pipeline
2. Click the `unit_tests` job
3. Right panel → **Download artifacts** → `reports/`

You'll find:
- `reports/junit.xml`    — machine-readable test results
- `reports/coverage/`   — HTML coverage report

### Environments

| Environment | How triggered | Approval |
|---|---|---|
| `staging` | Automatic on push to `main` | None |
| `production` | Automatic on push to `main` | **Manual click** required |

View at: **Deployments → Environments**

### CI Variables (GitLab UI → Settings → CI/CD → Variables)

Add these to avoid hardcoding secrets:

| Variable | Example value | Purpose |
|---|---|---|
| `CI_REGISTRY_USER` | `gitlab-ci-token` | Registry login |
| `CI_REGISTRY_PASSWORD` | `<token>` | Registry login |
| `STAGING_URL` | `https://staging.myapp.com` | Env URL |
| `PRODUCTION_URL` | `https://myapp.com` | Env URL |

---

## Local Development

```bash
# Install dependencies
pip install -r requirements.txt

# Run the app
python app.py                 # → http://localhost:5000

# Run tests
pytest test_app.py -v

# Lint
flake8 app.py --max-line-length=100
```

### App endpoints

| Endpoint | Method | Description |
|---|---|---|
| `/` | GET | App info JSON |
| `/health` | GET | Health check (used by Docker) |
| `/greet/<name>` | GET | Greeting message |
| `/add/<a>/<b>` | GET | Sum of two integers |

---

## Reference Links

- GitLab CI/CD docs: https://docs.gitlab.com/ee/ci/
- `.gitlab-ci.yml` reference: https://docs.gitlab.com/ee/ci/yaml/
- Pipelines: https://docs.gitlab.com/ee/ci/pipelines/
- Test reports: https://docs.gitlab.com/ee/ci/testing/
- Environments: https://docs.gitlab.com/ee/ci/environments/
