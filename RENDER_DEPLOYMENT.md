# Render Deployment Guide for FocusSphere

## Overview
This guide explains how to deploy FocusSphere (Spring Boot 3.3.2) to Render.com using Docker containerization.

## Files Created
- **Dockerfile** - Multi-stage Docker build configuration
- **render.yaml** - Render deployment manifest
- **.dockerignore** - Files to exclude from Docker build context
- **application-prod.properties** - Production environment configuration

## Deployment Steps

### Step 1: Push Code to GitHub
```bash
git add .
git commit -m "Add Render deployment configuration"
git push -u origin main
```

### Step 2: Create Render Account & Project
1. Go to https://dashboard.render.com
2. Sign up or log in with your GitHub account
3. Click **"New Web Service"**

### Step 3: Connect GitHub Repository
1. Click **"Connect GitHub"**
2. Authorize Render to access your GitHub repositories
3. Select **focusesphere** repository
4. Select **main** branch
5. Click **"Connect"**

### Step 4: Configure Web Service
- **Name**: focussphere
- **Environment**: Docker
- **Region**: Ohio (or your preferred region)
- **Branch**: main
- **Auto-deploy**: Enabled

### Step 5: Set Environment Variables (Optional - Already in render.yaml)
If not auto-detected from render.yaml, add:
- `SPRING_PROFILES_ACTIVE` = `prod`
- `JAVA_OPTS` = `-Xmx512m -Xms256m`
- `PORT` = `8080`

### Step 6: Deploy
Click **"Deploy"** and wait for the build process to complete.

## Deployment Details

### Build Process
- **Builder Stage**: Uses eclipse-temurin:17-jdk-alpine to compile with Maven
- **Runtime Stage**: Uses eclipse-temurin:17-jre-alpine (smaller image)
- **Build Time**: ~3-5 minutes for initial build
- **Image Size**: ~350-400 MB compressed

### Database Configuration
- **Type**: H2 (file-based)
- **Location**: `/tmp/focussphere_prod` on Render
- **Persistence**: Data survives service restarts BUT is lost on service rebuild
- **Recommendation**: Migrate to PostgreSQL for production persistence

### Security Notes
⚠️ **IMPORTANT**: Default admin credentials in application-prod.properties:
- Email: `admin@focussphere.com`
- Password: `ChangeThis@123`

**Action Required**: After deployment, immediately:
1. Access the application
2. Log in with default credentials
3. Change the password to a strong, secure one

### Health Check
- **Path**: `/h2-console`
- **Interval**: 30 seconds
- **Timeout**: 3 seconds
- **Start Period**: 40 seconds
- **Retries**: 3

### Port Configuration
- **Internal Port**: 8080 (defined in Spring Boot)
- **Render Port**: Automatically mapped to 8080

## Accessing Your Deployed Application
After successful deployment, access your app at:
```
https://focussphere-xxxxx.onrender.com
```
(Render will provide the exact URL in the dashboard)

## Monitoring & Logs
1. Go to your service dashboard on Render
2. Click **"Logs"** tab to view real-time application logs
3. Check **"Events"** for deployment history

## Scaling Options
- **Plan**: Starter (included) → Pro, Standard, or Premium for higher performance
- **Memory**: Currently set to 512MB in JAVA_OPTS
- **Upgrade**: Modify `JAVA_OPTS` environment variable or upgrade plan

## Database Migration to PostgreSQL (Optional but Recommended)
For production persistence, consider PostgreSQL:

1. Add PostgreSQL add-on in Render dashboard
2. Update `application-prod.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://postgres-host:5432/focussphere
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   ```
3. Redeploy

## Troubleshooting

### Build Fails
- Check Logs for Maven compilation errors
- Ensure Java 17 compatible code (no Java 21+ features)
- Verify all dependencies in pom.xml are accessible

### Application Won't Start
- Check environment variables are set correctly
- Verify `SPRING_PROFILES_ACTIVE=prod`
- Review logs for specific errors

### Deployment Takes Too Long
- Initial builds take 3-5 minutes due to Maven dependency resolution
- Subsequent deploys are faster (cached dependencies)
- Check Maven central repository status

## Auto-Deployment
The render.yaml file includes `autoDeploy: true`, which means:
- Any push to main branch automatically triggers a new deployment
- Disable in Render dashboard if you want manual deployments

## Health Endpoint
The application includes a health check at `/h2-console`. To verify your deployment:
```bash
curl https://focussphere-xxxxx.onrender.com/h2-console
```

## Support
For Render-specific issues: https://render.com/docs
For FocusSphere application issues: Check logs or contact the development team
