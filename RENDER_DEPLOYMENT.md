# FocusSphere - Render Deployment Guide

## Deployment Steps

### 1. Push to GitHub
Ensure your project is pushed to GitHub:
```bash
git add .
git commit -m "Add Docker and Render deployment configuration"
git push origin main
```

### 2. Create Render Service

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click **New +** → **Web Service**
3. Connect your GitHub repository
4. Configure the service:
   - **Name**: focussphere
   - **Runtime**: Docker
   - **Region**: Choose closest to you
   - **Branch**: main
   - **Dockerfile Path**: ./Dockerfile

### 3. Environment Variables (Optional - Already in application-prod.properties)
Set in Render dashboard if needed:
- `SPRING_PROFILES_ACTIVE=prod`
- `JAVA_OPTS=-Xmx512m -Xms256m`

### 4. Deploy
Click **Create Web Service** and Render will automatically build and deploy your application.

---

## Docker Build & Test Locally

### Build Docker Image
```bash
docker build -t focussphere:latest .
```

### Run with Docker
```bash
docker run -p 8080:8080 focussphere:latest
```

### Run with Docker Compose
```bash
docker-compose up
```

---

## Application Configuration

### Local Development (H2 Database)
```bash
SPRING_PROFILES_ACTIVE=local java -jar app.jar
```

### Production (Render)
```bash
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

---

## Important Notes for Render

1. **Database**: Currently uses H2 file-based database in `/tmp`
   - **Limitation**: Data will be lost when Render restarts the service
   - **Recommendation**: Migrate to PostgreSQL for persistent storage

2. **Memory**: Allocated 256MB-512MB
   - Adjust `JAVA_OPTS` if needed

3. **Security**: 
   - Change default admin credentials in `application-prod.properties`
   - Consider enabling HTTPS (Render provides it automatically)

4. **Admin Credentials** (Change immediately):
   - Email: `admin@focussphere.com`
   - Password: `ChangeThis@123`

---

## First Access

Once deployed on Render:
1. Visit: `https://focussphere-xxxxx.onrender.com`
2. Login with admin credentials
3. Change admin password immediately
4. Configure your rooms and users

---

## Monitoring

- View logs in Render dashboard
- Check application health at `/h2-console`
- Monitor CPU and memory usage in Render dashboard

---

## To Switch to PostgreSQL (Recommended for Production)

1. Add PostgreSQL add-on in Render
2. Update `application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://host:port/database
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```
3. Redeploy

---

For support, contact: admin@focussphere.com
