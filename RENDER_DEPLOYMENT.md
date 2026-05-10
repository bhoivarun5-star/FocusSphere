# FocusSphere - Render Deployment Guide

This document provides step-by-step instructions to deploy FocusSphere on Render.com

## Prerequisites

- Render.com account (https://render.com)
- GitHub repository with this code
- PostgreSQL or MySQL database (optional, but recommended for production)

## Deployment Steps

### 1. Create a Render Account and Connect GitHub

1. Go to https://render.com and create an account
2. Connect your GitHub repository to Render

### 2. Create a Web Service

1. In Render Dashboard, click **"New +"** → **"Web Service"**
2. Select your GitHub repository (FocusSphere)
3. Configure the following settings:

   **Basic Configuration:**
   - Name: `focussphere`
   - Environment: `Java`
   - Runtime: `Java 17`
   - Build Command: `mvn clean package -DskipTests -Prender`
   - Start Command: `java -jar target/focussphere-0.0.1-SNAPSHOT.jar --spring.profiles.active=render --server.port=$PORT`

   **Plan:** Choose appropriate plan (Starter/Standard/Pro)

### 3. Configure Environment Variables

Add the following environment variables in the Render Dashboard:

#### Database Configuration (Choose one option)

**Option A: Using Render PostgreSQL Database**
```
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://[your-db-host]:[your-db-port]/focussphere_db
DB_USERNAME=[your-username]
DB_PASSWORD=[your-password]
DB_POOL_SIZE=20
```

**Option B: Using External MySQL Database**
```
DB_DRIVER=com.mysql.cj.jdbc.Driver
DB_URL=jdbc:mysql://[your-db-host]:[your-db-port]/focussphere_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=[your-username]
DB_PASSWORD=[your-password]
DB_POOL_SIZE=20
```

**Option C: Using H2 Embedded Database (Dev/Demo)**
```
DB_DRIVER=org.h2.Driver
DB_URL=jdbc:h2:file:/tmp/focussphere_prod;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE
DB_USERNAME=sa
DB_PASSWORD=
DB_POOL_SIZE=10
```

#### Admin Credentials
```
ADMIN_NAME=System Admin
ADMIN_EMAIL=admin@focussphere.com
ADMIN_PASSWORD=ChangeThis@123
ADMIN_PHONE=9999999999
ADMIN_ROLL_NO=ADMIN001
```

#### Email Configuration (Optional)
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=[your-email@gmail.com]
MAIL_PASSWORD=[your-app-password]
MAIL_SMTP_AUTH=true
MAIL_STARTTLS_ENABLE=true
MAIL_STARTTLS_REQUIRED=true
```

#### Spring Profile
```
SPRING_PROFILES_ACTIVE=render
```

### 4. Create Database (if using external database)

If you're using an external database, create a database named `focussphere_db` with the credentials specified above.

### 5. Deploy

1. Click **"Create Web Service"** to start deployment
2. Monitor the build logs in the Render Dashboard
3. Once deployment is successful, you'll get a URL like `https://focussphere-xxxxx.onrender.com`

## Build and Deployment Process

### Build Artifacts

- **Framework:** Spring Boot 3.3.2
- **Runtime:** Java 17
- **Build Tool:** Maven
- **Output:** WAR/JAR file in `target/focussphere-0.0.1-SNAPSHOT.jar`

### Startup Sequence

1. Maven builds the application with the `render` profile
2. Spring Boot starts the embedded Tomcat server
3. Database migrations are applied (if using JPA DDL)
4. Admin user is seeded (if database is empty)
5. Application listens on the PORT environment variable

### Performance Notes

- Connection pool size: 20 (configurable via `DB_POOL_SIZE`)
- Session timeout: 30 minutes
- Compression: Enabled for responses > 1KB
- WebSocket: Enabled for real-time features

## Monitoring

### Health Check Endpoint

- Endpoint: `https://your-app.onrender.com/actuator/health`
- Response: JSON with application health status

### Logs

Access logs in the Render Dashboard:
- Runtime logs
- Build logs
- Error tracking

## Troubleshooting

### Common Issues

**1. Build Fails with "Maven not found"**
- Solution: Update the build command to use the correct Maven path
- Use: `mvn clean package -DskipTests -Prender`

**2. Application Crashes After Deploy**
- Check environment variables are set correctly
- Verify database connection parameters
- Check Spring profiles configuration

**3. Database Connection Issues**
- Verify DB_URL, DB_USERNAME, DB_PASSWORD are correct
- Ensure database is accessible from Render
- Check firewall rules if using external database

**4. JSP Views Not Rendering**
- Verify `spring.mvc.view.prefix=/WEB-INF/jsp/` is set
- Check JSP files exist in the WAR
- Ensure Tomcat Jasper dependencies are included

**5. Static Files Not Loading**
- Check CSS/JS files are in `src/main/webapp/`
- Verify paths in JSP files

## Database Management

### PostgreSQL Setup on Render

1. Create a PostgreSQL database on Render
2. Use the database URL provided by Render
3. Update `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` in environment variables
4. Set `DB_DRIVER=org.postgresql.Driver`

**Note:** PostgreSQL requires `spring-boot-starter-data-jpa` to include PostgreSQL driver.

### Database Backups

- Enable automatic backups in Render Database dashboard
- Set backup frequency to daily
- Download backups for local archival

## Post-Deployment

### First Login

1. Navigate to your deployed URL
2. Go to login page (usually `/login`)
3. Use admin credentials set in environment variables
4. Change default password immediately

### Initial Setup

1. Create user accounts
2. Configure rooms and focus sessions
3. Set up notification preferences
4. Test WebSocket functionality (real-time chat)

## Scaling

### Vertical Scaling
- Upgrade Render plan (Starter → Standard → Pro)
- Increase resource allocation in render.yaml

### Database Scaling
- For PostgreSQL: Use Render's database scaling options
- For MySQL: Set `DB_POOL_SIZE` to 20-30 for production

## Useful Commands

### Local Testing with Render Profile

```bash
# Run locally with render profile
.tools/apache-maven-3.9.14/bin/mvn.cmd spring-boot:run -Dspring-boot.run.profiles=render

# Or with JAR
java -jar target/focussphere-0.0.1-SNAPSHOT.jar --spring.profiles.active=render
```

### Maven Build

```bash
# Build for render
mvn clean package -DskipTests -Prender

# Build with tests
mvn clean package -Prender
```

## Security Considerations

1. **Never commit `.env` files** - Use Render environment variables
2. **Change default admin password** - Update in first login
3. **Use strong database passwords** - Generate secure passwords
4. **Enable HTTPS** - Render automatically provides HTTPS
5. **Database encryption** - Enable in PostgreSQL settings
6. **Regular backups** - Configure automated backups

## Support

For issues or questions:
- Check Render documentation: https://render.com/docs
- Spring Boot documentation: https://spring.io/projects/spring-boot
- FocusSphere GitHub Issues

---

**Last Updated:** May 2026
**Tested with:** Spring Boot 3.3.2, Java 17, Render Platform
