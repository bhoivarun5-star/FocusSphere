# Quick Start Guide - Local Testing & Render Deployment

## 🏠 Local Development & Testing

### Option 1: Run Locally with Maven (Development)

```bash
# Navigate to project root
cd FocusSphere

# Run with H2 embedded database (no setup needed)
.tools/apache-maven-3.9.14/bin/mvn.cmd clean spring-boot:run "-Dspring-boot.run.profiles=local"

# Access at: http://localhost:8080
```

**Default credentials (local profile):**
- Email: admin@focussphere.com
- Password: ChangeThis@123

---

### Option 2: Run with Docker Locally (Production-like)

```bash
# Build and run with docker-compose
docker-compose up --build

# Access at: http://localhost:8080

# Stop the service
docker-compose down

# View logs
docker-compose logs -f

# Rebuild only
docker-compose build

# Run without rebuilding
docker-compose up
```

**Advantages:**
- ✅ Tests production configuration
- ✅ Same environment as Render
- ✅ Verifies Dockerfile works
- ✅ Tests environment variables

---

### Option 3: Manual Docker Build & Run

```bash
# Build the Docker image
docker build -t focussphere:latest .

# Run the container
docker run -d \
  -p 8080:8080 \
  -e PORT=8080 \
  -e SPRING_PROFILES_ACTIVE=render \
  -e ADMIN_PASSWORD=MySecurePassword@123 \
  --name focussphere \
  focussphere:latest

# View logs
docker logs -f focussphere

# Stop the container
docker stop focussphere
docker rm focussphere
```

---

## 🚀 Deploy to Render (Step-by-Step)

### Step 1: Prepare Your Repository

```bash
# Make sure all changes are committed
git status

# Add all files
git add .

# Commit changes
git commit -m "Configure FocusSphere for Render deployment"

# Push to your repository (GitHub/GitLab)
git push origin main
```

---

### Step 2: Create Render Account & Connect Repository

1. Go to **render.com**
2. Click **Sign Up** (use GitHub/GitLab)
3. Authorize Render to access your repositories
4. Click **New +** → **Web Service**
5. Select your **FocusSphere** repository
6. Click **Connect**

---

### Step 3: Configure Service

Fill in these fields:

| Field | Value |
|-------|-------|
| **Name** | `focussphere` |
| **Environment** | `Docker` |
| **Region** | `Ohio` (or closest to you) |
| **Branch** | `main` |
| **Build Command** | *(leave blank - uses Dockerfile)* |
| **Start Command** | *(leave blank - uses Dockerfile)* |
| **Plan** | `Starter` (free) or `Standard` |

Click **Next**

---

### Step 4: Add Environment Variables

In the "Advanced" section, click **Add Environment Variable** for each:

**Required:**
```
PORT=8080
SPRING_PROFILES_ACTIVE=render
```

**Database (Choose One):**

**H2 (Embedded):**
```
DB_URL=jdbc:h2:file:/tmp/focussphere_prod;MODE=MySQL
DB_USERNAME=sa
DB_PASSWORD=
DB_DRIVER=org.h2.Driver
DB_PLATFORM=org.hibernate.dialect.H2Dialect
```

**PostgreSQL (Production):**
1. Create PostgreSQL on Render first
2. Get connection URL
3. Set variables:
```
DATABASE_URL=postgresql://user:password@host:port/focussphere
DB_DRIVER=org.postgresql.Driver
DB_PLATFORM=org.hibernate.dialect.PostgreSQL10Dialect
```

**Admin Configuration:**
```
ADMIN_NAME=System Admin
ADMIN_EMAIL=admin@focussphere.com
ADMIN_PHONE=9999999999
ADMIN_ROLL_NO=ADMIN001
ADMIN_PASSWORD=YourSecurePassword@123
```

**Optional - Mail:**
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

**Java:**
```
JAVA_OPTS=-Xms512m -Xmx1024m
```

---

### Step 5: Deploy!

Click **Create Web Service**

✅ Render will:
1. Clone your repository
2. Build the Docker image (~5-10 minutes)
3. Deploy the application
4. Provide you with a public URL

---

## 📝 Monitoring & Management

### View Logs
- **Render Dashboard** → Your service → **Logs** tab
- Real-time logs appear here

### Check Deployment Status
- **Events** tab shows deployment history
- **Metrics** tab shows CPU/Memory/Network

### Environment Variables
- **Settings** → **Environment Variables**
- Edit and restart service to apply changes

### Enable Auto-Deploy
- **Settings** → **Auto-Deploy** 
- Enable "Auto-Deploy on Push"
- Now deployment happens automatically on git push

---

## 🆘 Troubleshooting

### Docker Build Fails
```
Check: Maven dependencies are downloading
Solution: 
1. Check logs for specific error
2. Run locally first: docker-compose up --build
3. Verify pom.xml is valid
```

### Application Doesn't Start
```
Check: PORT environment variable
Check: Database configuration
Solution:
1. Review logs in Render Dashboard
2. Verify environment variables are set
3. Test locally with docker-compose first
```

### Health Check Failing
```
Check: Application on correct port
Check: Health endpoint working
Solution:
1. Verify PORT is set correctly
2. Wait a bit - takes 40+ seconds to start
3. Check logs for startup errors
```

### High Memory Usage
```
Solution: Adjust JAVA_OPTS
Reduce: -Xms512m -Xmx1024m
To: -Xms256m -Xmx512m
(May impact performance)
```

---

## 📋 Files Reference

| File | Purpose |
|------|---------|
| `Dockerfile` | Docker build configuration |
| `docker-compose.yml` | Local Docker testing |
| `render.yaml` | Render deployment config |
| `application-render.properties` | Render Spring profile |
| `DEPLOYMENT_GUIDE.md` | Detailed deployment guide |
| `RENDER_DEPLOYMENT_SUMMARY.md` | Summary of changes |
| `RENDER_ENV_TEMPLATE.txt` | Environment variables template |
| `.dockerignore` | Files to exclude from Docker build |

---

## ✅ Pre-Deployment Checklist

- [ ] Code committed and pushed
- [ ] Tested locally with `docker-compose up`
- [ ] Admin password changed
- [ ] Admin email updated
- [ ] Database option chosen (H2 or PostgreSQL)
- [ ] Environment variables prepared
- [ ] Repository connected to Render
- [ ] Service created on Render dashboard
- [ ] Environment variables set in Render
- [ ] Monitoring logs in place

---

## 🎯 Deployment Timelines

**Local Maven:**
- Startup: ~10 seconds
- Ready to use: Immediately

**Docker Build (First Time):**
- Build: 5-10 minutes
- Push: 1-2 minutes
- Start: 1-2 minutes
- **Total: 7-14 minutes**

**Docker Build (Subsequent):**
- Build: 2-5 minutes (uses cache)
- Push: 1-2 minutes
- Start: 1-2 minutes
- **Total: 4-9 minutes**

---

## 🔄 Common Workflows

### Update Code & Deploy
```bash
# Make changes
# Test locally
docker-compose up

# If working, commit
git add .
git commit -m "Feature: description"
git push origin main

# If auto-deploy enabled, Render automatically deploys
# Otherwise, manually trigger in Render Dashboard
```

### Change Admin Password
```bash
# Go to Render Dashboard > Settings > Environment Variables
# Update: ADMIN_PASSWORD=new_password
# Click "Save Changes"
# Application restarts automatically
```

### Switch to PostgreSQL
```bash
# Create PostgreSQL database on Render
# Get connection URL
# Update Render environment variables:
#   DATABASE_URL=postgresql://...
#   DB_DRIVER=org.postgresql.Driver
#   DB_PLATFORM=org.hibernate.dialect.PostgreSQL10Dialect
# Save and restart
```

---

## 📞 Support Resources

- [Render Documentation](https://render.com/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com)
- [Dockerfile Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

## 🎉 After Successful Deployment

1. **Access your app:**
   - URL: `https://your-service-name.onrender.com`

2. **Log in:**
   - Email: admin@focussphere.com (or your ADMIN_EMAIL)
   - Password: Your ADMIN_PASSWORD

3. **Next steps:**
   - Verify all features work
   - Set up custom domain (optional)
   - Configure backups
   - Monitor logs
   - Set up error tracking

---

**Last Updated:** May 2026  
**Ready for Production:** ✅ Yes
