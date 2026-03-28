#!/bin/bash
# Full validation: all tests (unit + integration, requires Docker), then start the backend.
# Usage: ./dev-full.sh
set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

trap 'echo; echo "==> Script exited with code $?. Press Enter to close..."; read -r' EXIT

#echo "==> Running frontend tests..."
#cd "$ROOT_DIR/frontend"
#npx ng test --watch=false

echo "==> Running all backend tests (integration tests require Docker)..."
cd "$ROOT_DIR/backend"
./mvnw test

echo "==> All tests passed. Starting backend..."
./mvnw spring-boot:run
