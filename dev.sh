#!/bin/bash
# Fast dev loop: unit tests only (no Docker), then start the backend.
# Usage: ./dev.sh
set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

trap 'echo; echo "==> Script exited with code $?. Press Enter to close..."; read -r' EXIT

echo "==> Running unit tests (excluding integration tests)..."
cd "$ROOT_DIR/backend"
./mvnw test -Dtest='!*IT'

echo "==> All unit tests passed. Starting backend..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
