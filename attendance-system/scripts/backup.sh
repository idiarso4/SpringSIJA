#!/bin/bash

# Configuration
BACKUP_ROOT="/backup"
DB_BACKUP_DIR="$BACKUP_ROOT/database"
FILES_BACKUP_DIR="$BACKUP_ROOT/files"
LOG_BACKUP_DIR="$BACKUP_ROOT/logs"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# Create backup directories if they don't exist
mkdir -p "$DB_BACKUP_DIR" "$FILES_BACKUP_DIR" "$LOG_BACKUP_DIR"

# Database backup function
backup_database() {
    echo "Starting database backup..."
    
    # Backup all databases
    docker exec attendance-system_db_1 pg_dumpall -U postgres > \
        "$DB_BACKUP_DIR/full_backup_$TIMESTAMP.sql"
    
    # Compress backup
    gzip "$DB_BACKUP_DIR/full_backup_$TIMESTAMP.sql"
    
    echo "Database backup completed"
}

# File storage backup function
backup_files() {
    echo "Starting file storage backup..."
    
    # Backup uploaded files
    tar -czf "$FILES_BACKUP_DIR/files_$TIMESTAMP.tar.gz" /app/uploads/
    
    echo "File storage backup completed"
}

# Log files backup function
backup_logs() {
    echo "Starting log files backup..."
    
    # Backup log files
    tar -czf "$LOG_BACKUP_DIR/logs_$TIMESTAMP.tar.gz" /app/logs/
    
    echo "Log files backup completed"
}

# Cleanup old backups function
cleanup_old_backups() {
    echo "Cleaning up old backups..."
    
    # Remove database backups older than retention period
    find "$DB_BACKUP_DIR" -name "full_backup_*.sql.gz" -mtime +$RETENTION_DAYS -delete
    
    # Remove file backups older than retention period
    find "$FILES_BACKUP_DIR" -name "files_*.tar.gz" -mtime +$RETENTION_DAYS -delete
    
    # Remove log backups older than retention period
    find "$LOG_BACKUP_DIR" -name "logs_*.tar.gz" -mtime +$RETENTION_DAYS -delete
    
    echo "Cleanup completed"
}

# Upload to remote storage function (optional)
upload_to_remote() {
    if [ -n "$REMOTE_BACKUP_URL" ]; then
        echo "Uploading backups to remote storage..."
        
        # Upload database backup
        rclone copy "$DB_BACKUP_DIR/full_backup_$TIMESTAMP.sql.gz" "$REMOTE_BACKUP_URL/database/"
        
        # Upload file backup
        rclone copy "$FILES_BACKUP_DIR/files_$TIMESTAMP.tar.gz" "$REMOTE_BACKUP_URL/files/"
        
        # Upload log backup
        rclone copy "$LOG_BACKUP_DIR/logs_$TIMESTAMP.tar.gz" "$REMOTE_BACKUP_URL/logs/"
        
        echo "Remote upload completed"
    fi
}

# Main backup process
main() {
    echo "Starting backup process at $(date)"
    
    # Perform backups
    backup_database
    backup_files
    backup_logs
    
    # Clean up old backups
    cleanup_old_backups
    
    # Upload to remote storage if configured
    upload_to_remote
    
    echo "Backup process completed at $(date)"
}

# Execute main function
main > "$BACKUP_ROOT/backup_$TIMESTAMP.log" 2>&1
