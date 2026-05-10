# FocusSphere - Render Deployment Checklist

## Project Analysis Summary

### ✅ Project Configuration
- **Framework:** Spring Boot 3.3.2
- **Java Version:** 17
- **Build Tool:** Maven
- **Packaging:** WAR (converted to executable JAR)
- **Database:** H2 (embedded), MySQL/PostgreSQL compatible
- **Additional Features:** 
  - WebSocket support (real-time messaging)
  - Spring Security
  - JPA/Hibernate ORM
  - JSP view rendering
  - Spring Actuator (health checks)

### 📦 Files Created for Render Deployment

1. **render.yaml** - Render deployment configuration
   - Java 17 runtime
   - Maven build profile
   - Environment variable configuration
   - Health check endpoint
   - Auto-deploy on push

2. **Procfile** - Alternative deployment configuration
   - Specifies how to run the application on Render

3. **RENDER_DEPLOYMENT.md** - Comprehensive deployment guide
   - Step-by-step instructions
   - Environment variable configuration
   - Database setup options
   - Troubleshooting guide

4. **pom.xml (Updated)** - Added Maven profiles
   - `local` profile (default, for local development)
   - `prod` profile (general production)
   - `render` profile (Render.com specific)

5. **.gitignore (Updated)** - Added local tools exclusion
   - Prevents committing local Maven installation

### 🔧 What You Need to Do on Render

#### Step 1: Prepare Your Git Repository
```bash
# Make sure all changes are committed
git add .
git commit -m "Add Render deployment configuration"
git push origin main
```

#### Step 2: Create Render Account & Connect GitHub
- Go to https://render.com
- Sign up for free account
- Click "New" → "Web Service"
- Connect your GitHub repository

#### Step 3: Configure Build & Start Commands
The configuration is already in `render.yaml`:
- **Build Command:** `mvn clean package -DskipTests -Prender`
- **Start Command:** `java -jar target/focussphere-0.0.1-SNAPSHOT.jar --spring.profiles.active=render`

#### Step 4: Set Environment Variables in Render Dashboard

**Choose ONE database option:**

**Option A: H2 Embedded (Simplest - Good for Demo)**
```
DB_DRIVER=org.h2.Driver
DB_URL=jdbc:h2:file:/tmp/focussphere_prod;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE
DB_USERNAME=sa
DB_PASSWORD=
```

**Option B: PostgreSQL (Recommended - Persistent)**
1. Create PostgreSQL database on Render
2. Get connection details from Render dashboard
3. Set environment variables:
```
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://[host]:[port]/focussphere_db
DB_USERNAME=[username]
DB_PASSWORD=[password]
```
4. Add PostgreSQL driver to pom.xml (if not already present)

**Option C: External MySQL**
```
DB_DRIVER=com.mysql.cj.jdbc.Driver
DB_URL=jdbc:mysql://[host]:[port]/focussphere_db?createDatabaseIfNotExist=true&useSSL=false
DB_USERNAME=[username]
DB_PASSWORD=[password]
```

**Admin Credentials:**
```
ADMIN_NAME=System Admin
ADMIN_EMAIL=admin@focussphere.com
ADMIN_PASSWORD=ChangeThis@123
ADMIN_PHONE=9999999999
ADMIN_ROLL_NO=ADMIN001
```

**Email Configuration (Optional):**
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=[your-email]
MAIL_PASSWORD=[your-app-password]
```

**Spring Profile:**
```
SPRING_PROFILES_ACTIVE=render
```

#### Step 5: Deploy
1. Click "Create Web Service" in Render
2. Monitor build logs
3. Once deployed, note your URL: `https://focussphere-xxxxx.onrender.com`

### 📊 Database Recommendations

| Use Case | Database | Pros | Cons |
|----------|----------|------|------|
| **Demo/Testing** | H2 Embedded | Fast setup, no external deps | Data lost on restart |
| **Production** | PostgreSQL | Persistent, free tier available | Need external service |
| **Existing DB** | MySQL | Familiar, proven | Need external hosting |

### 🔐 Security Checklist

- [ ] Change `ADMIN_PASSWORD` in environment variables
- [ ] Use strong, unique database passwords
- [ ] Enable database backups (if using Render DB)
- [ ] Keep dependencies updated
- [ ] Review application logs for errors
- [ ] Test email configuration (if enabled)

### 🚀 Post-Deployment Steps

1. **Access Application**
   - Go to: `https://your-focussphere-app.onrender.com`

2. **First Login**
   - Use admin credentials from environment variables
   - Change default password immediately

3. **Verify Features**
   - Test login/registration
   - Create a room
   - Join as different user
   - Test WebSocket (real-time chat)
   - Check notifications

4. **Monitor Application**
   - Check health endpoint: `/actuator/health`
   - Monitor Render logs in dashboard
   - Set up alerts (if available)

### 📝 Configuration Files Overview

#### application-render.properties
- Optimized for Render environment
- Uses environment variables for sensitive data
- Disables H2 console (security)
- Configures logging for cloud
- Enables Actuator health checks

#### render.yaml
```yaml
- Runtime: Java 17
- Build: Maven with render profile
- Health Check: /actuator/health
- Auto-Deploy: Enabled on git push
- Disk: 1GB persistent storage
```

#### pom.xml Profiles
```xml
<profile id="render">
  <!-- Activates application-render.properties -->
  <!-- Use: mvn clean package -Prender -->
</profile>
```

### 🆘 Troubleshooting

**Build Fails:**
- Check logs in Render dashboard
- Verify Maven can find dependencies
- Ensure Java 17 is specified

**App Crashes After Deploy:**
- Check Render logs
- Verify all environment variables are set
- Test database connection
- Ensure database is initialized

**Database Connection Error:**
- Verify DB_URL, DB_USERNAME, DB_PASSWORD
- Check if database exists and is accessible
- Test connection locally first

**JSP Views Not Rendering:**
- Check that JSP files are in target/
- Verify spring.mvc.view.prefix setting
- Ensure Tomcat Jasper dependencies included

**Slow Performance:**
- Increase database pool size: `DB_POOL_SIZE=30`
- Upgrade Render plan to Standard or Pro
- Enable response compression (already done)

### 📞 Support Resources

- **Render Docs:** https://render.com/docs
- **Spring Boot:** https://spring.io/projects/spring-boot
- **Maven:** https://maven.apache.org
- **Application Logs:** Render Dashboard → Logs

### ✨ Optional Enhancements

1. **Custom Domain**
   - In Render Dashboard: Settings → Custom Domain
   - Add your domain and configure DNS

2. **SSL Certificate**
   - Render automatically provides HTTPS
   - Automatically renewed

3. **Scheduled Tasks**
   - FocusSphere has `@EnableScheduling`
   - Configure via environment variables if needed

4. **Database Backups**
   - If using Render PostgreSQL
   - Enable daily backups in database settings

### 📚 Quick Commands for Local Testing

```bash
# Test with render profile locally
mvn spring-boot:run -Dspring-boot.run.profiles=render

# Build JAR for render
mvn clean package -DskipTests -Prender

# Run built JAR
java -jar target/focussphere-0.0.1-SNAPSHOT.jar --spring.profiles.active=render
```

---

## ✅ Deployment Ready

Your project is now configured for Render deployment. Follow the Render deployment guide for step-by-step instructions.

**Good luck with your deployment! 🚀**
