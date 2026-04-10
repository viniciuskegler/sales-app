#!/bin/bash
# Builds and pushes both the backend and frontend Docker images to ECR.
# Requires AWS CLI configured with valid credentials.
set -e
trap 'echo; read -r -p "Script failed. Press Enter to close..."' ERR

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
REGION=$(aws configure get region)
REGISTRY="$ACCOUNT.dkr.ecr.$REGION.amazonaws.com"

echo "==> Authenticating Docker to ECR..."
aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$REGISTRY"

echo "==> Building backend image..."
docker build -t salesapp-backend "$ROOT_DIR/backend"
docker tag salesapp-backend:latest "$REGISTRY/salesapp-backend:latest"

echo "==> Building frontend image..."
docker build -t salesapp-frontend "$ROOT_DIR/frontend"
docker tag salesapp-frontend:latest "$REGISTRY/salesapp-frontend:latest"

echo "==> Pushing images..."
docker push "$REGISTRY/salesapp-backend:latest"
docker push "$REGISTRY/salesapp-frontend:latest"

echo "==> Done. Both App Runner services will redeploy automatically."

read -r -p "Press Enter to close..."
