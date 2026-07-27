#!/bin/bash
# Hyperlofy Database Backup Script
set -e

BACKUP_DIR="/var/backups/hyperlofy"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/hyperlofy_backup_${TIMESTAMP}.sql.gz"

mkdir -p ${BACKUP_DIR}

echo "[INFO] Starting Hyperlofy database backup: ${BACKUP_FILE}"
PGPASSWORD="${POSTGRES_PASSWORD}" pg_dump -h "${POSTGRES_HOST:-localhost}" -U "${POSTGRES_USER:-hyperlofy_admin}" "${POSTGRES_DB:-hyperlofy_db}" | gzip > "${BACKUP_FILE}"

echo "[SUCCESS] Database backup completed successfully: ${BACKUP_FILE}"
