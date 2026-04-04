#!/bin/bash
# Builds the backend Docker image and pushes it to ECR.
# Requires AWS CLI configured with valid credentials.
set -e
trap 'echo; read -r -p "Script failed. Press Enter to close..."' ERR

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
REGION=$(aws configure get region)
ECR_URI="$ACCOUNT.dkr.ecr.$REGION.amazonaws.com/salesapp-backend"

echo "==> Authenticating Docker to ECR..."
aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$ECR_URI"

echo "==> Building image..."
docker build -t salesapp-backend "$ROOT_DIR/backend"

echo "==> Pushing to $ECR_URI..."
docker tag salesapp-backend:latest "$ECR_URI:latest"
docker push "$ECR_URI:latest"

echo "==> Done. App Runner will redeploy automatically."

read -r -p "Press Enter to close..."
