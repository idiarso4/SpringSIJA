#!/bin/bash

# Configuration
BACKUP_ROOT="/backup"
DB_BACKUP_DIR="$BACKUP_ROOT/database"
FILES_BACKUP_DIR="$BACKUP_ROOT/files"
LOG_BACKUP_DIR="$BACKUP_ROOT/logs"

# Function to show usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo "Options:"
    echo "  -d, --date DATE    Backup date to restore (format: YYYYMMDD_HHMMSS)"
    echo "  -t, --type TYPE    Type of backup to restore (db|files|logs|all)"
    echo "  -h, --help         Show this help message"
    exit 1
}

# Function to restore database
restore_database() {
    local backup_date=$1
    local backup_file="$DB_BACKUP_DIR/full_backup_$backup_date.sql.gz"
    
    if [ ! -f "$backup_file" ]; then
        echo "Error: Database backup file not found: $backup_file"
        exit 1
    }
    
    echo "Restoring database from $backup_file..."
    
    # Stop the application container
    docker-compose stop app
    
    # Drop existing database
    docker exec attendance-system_db_1 dropdb -U postgres --if-exists attendance_db
    
    # Create new database
    docker exec attendance-system_db_1 createdb -U postgres attendance_db
    
    # Restore from backup
    gunzip -c "$backup_file" | docker exec -i attendance-system_db_1 psql -U postgres attendance_db
    
    # Start the application container
    docker-compose start app
    
    echo "Database restore completed"
}

# Function to restore files
restore_files() {
    local backup_date=$1
    local backup_file="$FILES_BACKUP_DIR/files_$backup_date.tar.gz"
    
    if [ ! -f "$backup_file" ]; then
        echo "Error: Files backup not found: $backup_file"
        exit 1
    }
    
    echo "Restoring files from $backup_file..."
    
    # Clear existing files
    rm -rf /app/uploads/*
    
    # Restore from backup
    tar -xzf "$backup_file" -C /
    
    echo "Files restore completed"
}

# Function to restore logs
restore_logs() {
    local backup_date=$1
    local backup_file="$LOG_BACKUP_DIR/logs_$backup_date.tar.gz"
    
    if [ ! -f "$backup_file" ]; then
        echo "Error: Logs backup not found: $backup_file"
        exit 1
    }
    
    echo "Restoring logs from $backup_file..."
    
    # Clear existing logs
    rm -rf /app/logs/*
    
    # Restore from backup
    tar -xzf "$backup_file" -C /
    
    echo "Logs restore completed"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -d|--date)
            BACKUP_DATE="$2"
            shift
            shift
            ;;
        -t|--type)
            RESTORE_TYPE="$2"
            shift
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Unknown option: $1"
            usage
            ;;
    esac
done

# Validate arguments
if [ -z "$BACKUP_DATE" ] || [ -z "$RESTORE_TYPE" ]; then
    echo "Error: Both date and type are required"
    usage
fi

# Main restore process
main() {
    echo "Starting restore process at $(date)"
    
    case $RESTORE_TYPE in
        db)
            restore_database "$BACKUP_DATE"
            ;;
        files)
            restore_files "$BACKUP_DATE"
            ;;
        logs)
            restore_logs "$BACKUP_DATE"
            ;;
        all)
            restore_database "$BACKUP_DATE"
            restore_files "$BACKUP_DATE"
            restore_logs "$BACKUP_DATE"
            ;;
        *)
            echo "Error: Invalid restore type. Must be one of: db, files, logs, all"
            exit 1
            ;;
    esac
    
    echo "Restore process completed at $(date)"
}

# Execute main function
main > "$BACKUP_ROOT/restore_$(date +%Y%m%d_%H%M%S).log" 2>&1
