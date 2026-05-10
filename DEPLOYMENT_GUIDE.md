# FocusSphere Deployment Guide for Render.com

## Overview

This guide walks you through deploying FocusSphere to Render.com, a modern cloud platform that supports Docker-based deployments.

## ✅ Changes Made for Production Deployment

### 1. **pom.xml Updates**
- ✓ Changed packaging from **WAR** to **JAR** (better for cloud deployments)
- ✓ Added explicit Java compiler properties (source/target 17)
- ✓ Added Spring Boot Actuator dependency for health checks
- ✓ Optimized build configuration for cloud environments

### 2. **Application Configuration**
- ✓ **application.properties** - Added `server.port=${PORT:8080}` to support Render's PORT environment variable
- ✓ **application-prod.properties** - Complete production setup with environment variable support
- ✓ **application-render.properties** - NEW Render-specific profile with optimized settings

### 3. **Docker Support**
- ✓ **Dockerfile** - Multi-stage build for optimal image size and security
  - Stage 1: Maven build in full JDK image
  - Stage 2: Runtime using lightweight Alpine Linux
  - Non-root user execution for security
  - Health check endpoint configured
  
- ✓ **.dockerignore** - Optimized Docker build context

### 4. **Render Configuration**
- ✓ **render.yaml** - Complete Render deployment configuration
- ✓ Environment variables pre-configured
- ✓ Health check endpoint configured
- ✓ Graceful shutdown settings

### 5. **Java Application Updates**
- ✓ **FocusSphereApplication.java** - Removed WAR-specific code (SpringBootServletInitializer)
  - Now uses pure embedded Tomcat (JAR mode)

### 6. **Git Configuration**
- ✓ **.gitignore** - Updated with Docker and build artifact exclusions

---

## 🚀 Deployment Steps

### Step 1: Prepare Your Git Repository

```bash
# Initialize git if not already done
git init

# Add all files
git add .

# Commit changes
git commit -m "Configure FocusSphere for Render deployment"

# Push to your repository (GitHub, GitLab, etc.)
git push origin main
```

### Step 2: Create Render Account

1. Go to [render.com](https://render.com)
2. Sign up for a free account
3. Connect your Git repository (GitHub/GitLab)

### Step 3: Create a New Web Service on Render

1. **Dashboard** → **New Web Service**
2. **Connect a repository** → Select your FocusSphere repository
3. Configure the service:

**Basic Settings:**
- **Name:** `focussphere` (or your preferred name)
- **Environment:** `Docker`
- **Region:** Choose closest to your users (default: Ohio is fine)
- **Branch:** `main` (or your deployment branch)
- **Build Command:** Leave default (uses Dockerfile)
- **Start Command:** Leave default (uses Dockerfile ENTRYPOINT)

**Instance Type:**
- **Plan:** Starter or Standard (Starter tier is free)

### Step 4: Configure Environment Variables

In Render Dashboard, go to **Environment** → **Environment Variables** and set:

**Required (Production Security):**
```
PORT=8080
SPRING_PROFILES_ACTIVE=render
```

**Database (Choose One):**

**Option A: H2 (Embedded - Default)**
```
DB_URL=jdbc:h2:file:/tmp/focussphere_prod;MODE=MySQL
DB_USERNAME=sa
DB_PASSWORD=
DB_DRIVER=org.h2.Driver
DB_PLATFORM=org.hibernate.dialect.H2Dialect
```

**Option B: PostgreSQL (Recommended for Production)**
```
DATABASE_URL=postgresql://user:password@host:port/focussphere
DB_DRIVER=org.postgresql.Driver
DB_PLATFORM=org.hibernate.dialect.PostgreSQL10Dialect
```

**Admin Configuration (CHANGE THESE!):**
```
ADMIN_NAME=System Admin
ADMIN_EMAIL=admin@focussphere.com
ADMIN_PHONE=9999999999
ADMIN_ROLL_NO=ADMIN001
ADMIN_PASSWORD=YourSecurePassword@123
```

**Optional - Mail Configuration:**
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_SMTP_AUTH=true
MAIL_STARTTLS_ENABLE=true
MAIL_STARTTLS_REQUIRED=true
```

**Java/JVM:**
```
JAVA_OPTS=-Xms512m -Xmx1024m
```

### Step 5: Deploy

1. Click **Create Web Service**
2. Render will:
   - Clone your repository
   - Build the Docker image
   - Deploy the application
3. Monitor the build logs in the Dashboard
4. Once deployed, you'll get a public URL: `https://focussphere-xxxxx.onrender.com`

---

## 📋 Environment Variable Reference

| Variable | Default | Purpose |
|----------|---------|---------|
| `PORT` | 8080 | Server port (set by Render) |
| `SPRING_PROFILES_ACTIVE` | render | Spring Boot profile to use |
| `DB_URL` | H2 file DB | Database connection URL |
| `DB_USERNAME` | sa | Database username |
| `DB_PASSWORD` | empty | Database password |
| `ADMIN_PASSWORD` | ChangeThis@123 | Admin account password ⚠️ CHANGE THIS |
| `JAVA_OPTS` | -Xms512m -Xmx1024m | JVM memory settings |

---

## 🔒 Security Checklist

Before deploying to production:

- [ ] ✅ Change `ADMIN_PASSWORD` to a strong password
- [ ] ✅ Set secure database credentials
- [ ] ✅ Configure HTTPS (Render provides free SSL)
- [ ] ✅ Review and update admin email
- [ ] ✅ Set up mail configuration if using email features
- [ ] ✅ Use PostgreSQL instead of H2 for persistent data
- [ ] ✅ Enable Render's error tracking
- [ ] ✅ Configure automatic backups for database
- [ ] ✅ Monitor application logs regularly

---

## 🐳 Docker Build Details

The Dockerfile uses a **multi-stage build** process:

**Stage 1 (Builder):**
- Uses Maven to compile and package the application
- Downloads all dependencies
- Creates a JAR file

**Stage 2 (Runtime):**
- Uses lightweight `eclipse-temurin:17-jre-alpine` image
- Copies only the built JAR
- Creates non-root user for security
- Exposes port 8080
- Includes health check

**Benefits:**
- ✓ Smaller final image (~400-500MB)
- ✓ No build tools in production image
- ✓ Faster deployment
- ✓ Better security

---

## 🆘 Troubleshooting

### Build Fails: "Java compilation error"
- Check that pom.xml has correct Java version (17)
- Verify all source files are valid Java

### Application Crashes: "Port already in use"
- PORT environment variable should be set by Render automatically
- Check that `server.port=${PORT:8080}` is in application-render.properties

### Database Connection Error
- Verify DB_URL, DB_USERNAME, DB_PASSWORD are correctly set
- For H2, /tmp directory must exist (it does on Render)
- For PostgreSQL, ensure DATABASE_URL follows correct format

### Health Check Failing
- Application must be running on the correct PORT
- Health check endpoint: `/health` (provided by Spring Boot Actuator)
- Check logs: Dashboard → **Logs** tab

### Deployment Takes Too Long
- Initial deployment can take 5-10 minutes for first Docker build
- Subsequent deployments are faster (cached layers)
- Check logs for Maven dependency downloads

---

## 📊 Monitoring & Logs

In Render Dashboard:

1. **Logs** tab - View application logs in real-time
2. **Events** tab - Deployment history
3. **Metrics** tab - CPU, Memory, Network usage
4. **Settings** → **Auto-Deploy** - Enable for automatic deployment on git push

---

## 🔄 Continuous Deployment Setup

To enable automatic deployment on every git push:

1. Render Dashboard → Your service
2. Settings → Auto-Deploy
3. Enable "Auto-Deploy on Push"
4. Now every commit to your main branch automatically deploys

---

## 💾 Database Persistence

### H2 (Default)
- File stored at `/tmp/focussphere_prod`
- Persists during service uptime
- Data lost on restart/redeployment

### PostgreSQL (Recommended)
- Full managed database
- Persistent across deployments
- Available as Render add-on

To add PostgreSQL:
1. Dashboard → **Create New** → **PostgreSQL**
2. Configure database name: `focussphere`
3. Update environment variables with connection details
4. Redeploy application

---

## 📈 Performance Tips

1. **JAR vs WAR** - Now using JAR for faster startup
2. **Actuator** - Added for better health monitoring
3. **Connection Pooling** - HikariCP configured with optimal settings
4. **Compression** - Enabled for responses > 1KB
5. **JVM Settings** - Default 512MB-1GB memory

---

## 🎯 Next Steps After Deployment

1. ✅ Access your app at the Render-provided URL
2. ✅ Log in with admin credentials
3. ✅ Verify all features work correctly
4. ✅ Set up error monitoring (Sentry, New Relic, etc.)
5. ✅ Configure custom domain (if desired)
6. ✅ Set up database backups
7. ✅ Monitor logs and metrics

---

## 📚 Additional Resources

- [Render Documentation](https://render.com/docs)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Dockerfile Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

---

## ❓ Support

For issues or questions:
1. Check Render Dashboard logs
2. Review this deployment guide
3. Consult Spring Boot documentation
4. Visit Render community forums

---

**Last Updated:** May 2026  
**FocusSphere Version:** 0.0.1-SNAPSHOT
