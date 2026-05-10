# Deployment Files Created for Render

This directory now contains all necessary files to deploy FocusSphere on Render:

## New Files Added:

1. **Dockerfile** - Multi-stage Docker build configuration
   - Java 17 alpine base image for minimal size
   - Non-root user for security
   - Health checks included
   
2. **.dockerignore** - Excludes unnecessary files from Docker build context
   - Reduces build time and image size
   - Improves security by not including IDE files, git data, etc.

3. **docker-compose.yml** - Local Docker Compose configuration
   - For testing the container locally before deployment
   - Includes volume persistence and health checks

4. **render.yaml** - Render deployment configuration
   - Specifies how Render should build and run your app
   - Configures environment variables and health checks

5. **src/main/resources/application-prod.properties** - Production environment configuration
   - H2 database configured for `/tmp` (temporary storage)
   - Optimized logging levels
   - Production-ready settings

6. **RENDER_DEPLOYMENT.md** - Complete deployment guide
   - Step-by-step instructions for deploying on Render
   - Docker commands for local testing
   - Important security and configuration notes

---

## Quick Start - Deploy on Render

1. Commit and push to GitHub:
```bash
git add .
git commit -m "Add Docker and Render deployment configuration"
git push origin main
```

2. Go to https://dashboard.render.com
3. Connect your GitHub repository
4. Create new Web Service with Docker runtime
5. Render will automatically build and deploy!

---

## Test Locally First

Build and run locally:
```bash
docker build -t focussphere:latest .
docker run -p 8080:8080 focussphere:latest
```

Then visit: http://localhost:8080

---

## Important Security Note

**Change the default admin credentials immediately after first login:**
- Default email: admin@focussphere.com
- Default password: ChangeThis@123

Update in `application-prod.properties` before deploying to production!
