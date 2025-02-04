# Deployment Guide

This guide provides detailed instructions for deploying the Face Recognition Attendance System in a production environment.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [System Requirements](#system-requirements)
3. [Installation Steps](#installation-steps)
4. [Configuration](#configuration)
5. [Security Considerations](#security-considerations)
6. [Monitoring Setup](#monitoring-setup)
7. [Backup Procedures](#backup-procedures)
8. [Troubleshooting](#troubleshooting)

## Prerequisites

### Software Requirements
- Docker Engine 20.10+
- Docker Compose 2.0+
- PostgreSQL 14+
- OpenCV 4.x
- NGINX or similar reverse proxy
- SSL certificate

### System Requirements
- CPU: 4+ cores
- RAM: 8GB minimum (16GB recommended)
- Storage: 100GB+ SSD
- Network: 100Mbps+ with stable internet connection

## Installation Steps

### 1. Initial Server Setup
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install required packages
sudo apt install -y docker.io docker-compose nginx certbot python3-certbot-nginx

# Start and enable Docker
sudo systemctl start docker
sudo systemctl enable docker
```

### 2. Clone and Build Application
```bash
# Clone repository
git clone https://github.com/yourusername/attendance-system.git
cd attendance-system

# Build Docker images
docker-compose build
```

### 3. SSL Certificate Setup
```bash
# Install SSL certificate
sudo certbot --nginx -d your-domain.com
```

### 4. Configure NGINX
Create `/etc/nginx/sites-available/attendance-system`:
```nginx
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /ws {
        proxy_pass http://localhost:8080/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

Enable the site:
```bash
sudo ln -s /etc/nginx/sites-available/attendance-system /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## Configuration

### 1. Environment Variables
Create `.env` file:
```properties
# Database
POSTGRES_DB=attendance_db
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_very_long_secure_jwt_secret

# Email
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_specific_password

# File Storage
FILE_STORAGE_PATH=/app/uploads
```

### 2. Application Properties
Update `application-prod.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://db:5432/${POSTGRES_DB}
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

logging:
  config: classpath:logback-spring.xml
```

## Security Considerations

### 1. Firewall Configuration
```bash
# Allow only necessary ports
sudo ufw allow ssh
sudo ufw allow http
sudo ufw allow https
sudo ufw enable
```

### 2. Database Security
- Use strong passwords
- Regular security updates
- Enable SSL for database connections
- Implement IP whitelisting

### 3. Application Security
- Keep dependencies updated
- Regular security audits
- Implement rate limiting
- Enable CORS protection

## Monitoring Setup

### 1. Prometheus Configuration
Create `prometheus.yml`:
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'attendance-system'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

### 2. Grafana Dashboard
- Import provided dashboard templates
- Set up alerts for:
  - High CPU/Memory usage
  - Error rate spikes
  - Slow response times
  - Failed login attempts

## Backup Procedures

### 1. Database Backup
Create backup script `backup.sh`:
```bash
#!/bin/bash
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/database"

# Create backup
docker exec attendance-system_db_1 pg_dump -U postgres attendance_db > \
    "$BACKUP_DIR/backup_$TIMESTAMP.sql"

# Compress backup
gzip "$BACKUP_DIR/backup_$TIMESTAMP.sql"

# Remove backups older than 30 days
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +30 -delete
```

### 2. File Storage Backup
```bash
#!/bin/bash
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/files"

# Backup uploaded files
tar -czf "$BACKUP_DIR/files_$TIMESTAMP.tar.gz" /app/uploads/

# Remove old backups
find $BACKUP_DIR -name "files_*.tar.gz" -mtime +30 -delete
```

## Troubleshooting

### Common Issues

1. **Application Won't Start**
   - Check logs: `docker-compose logs app`
   - Verify database connection
   - Check memory usage

2. **Slow Performance**
   - Monitor resource usage
   - Check database indexes
   - Analyze slow queries

3. **Face Recognition Issues**
   - Verify OpenCV installation
   - Check lighting conditions
   - Validate image quality

### Health Checks

```bash
# Check application health
curl -f http://localhost:8080/actuator/health

# Check database connection
docker exec attendance-system_db_1 pg_isready -U postgres

# View logs
docker-compose logs --tail=100 app
```

## Support

For additional support:
- Email: support@attendance-system.com
- Documentation: https://docs.attendance-system.com
- Issue Tracker: https://github.com/yourusername/attendance-system/issues
