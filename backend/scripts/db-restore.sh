#!/bin/bash
# Hyperlofy Database Restore Script
set -e

if [ -z "$1" ]; then
  echo "[ERROR] Usage: ./db-restore.sh <path_to_backup_file.sql.gz>"
  exit 1
fi

BACKUP_FILE="$1"

echo "[INFO] Restoring Hyperlofy database from: ${BACKUP_FILE}"
gunzip -c "${BACKUP_FILE}" | PGPASSWORD="${POSTGRES_PASSWORD}" psql -h "${POSTGRES_HOST:-localhost}" -U "${POSTGRES_USER:-hyperlofy_admin}" "${POSTGRES_DB:-hyperlofy_db}"

echo "[SUCCESS] Database restoration completed successfully."
