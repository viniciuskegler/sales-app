#!/bin/bash
# Builds and pushes both the backend and frontend Docker images to ECR.
# Requires AWS CLI configured with valid credentials.
set -e
trap 'echo; read -r -p "Script failed. Press Enter to close..."' ERR

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
REGION="${AWS_DEFAULT_REGION:-$(aws configure get region)}"
REGISTRY="$ACCOUNT.dkr.ecr.$REGION.amazonaws.com"

if [ -z "$REGION" ]; then
    echo "ERROR: AWS region not set. Configure it with 'aws configure' or set AWS_DEFAULT_REGION."
    exit 1
fi

echo "==> Authenticating Docker to ECR..."
aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$REGISTRY"

CACHEBUST=$(date +%s)
BUILD_TIME=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

echo "==> Building backend image..."
docker build --build-arg CACHEBUST="$CACHEBUST" -t salesapp-backend "$ROOT_DIR/backend"
docker tag salesapp-backend:latest "$REGISTRY/salesapp-backend:latest"

echo "==> Building frontend image..."
docker build --build-arg CACHEBUST="$CACHEBUST" --build-arg BUILD_TIME="$BUILD_TIME" -t salesapp-frontend "$ROOT_DIR/frontend"
docker tag salesapp-frontend:latest "$REGISTRY/salesapp-frontend:latest"

echo "==> Pushing images..."
docker push "$REGISTRY/salesapp-backend:latest"
docker push "$REGISTRY/salesapp-frontend:latest"

echo "==> Triggering App Runner deployments..."
BACKEND_ARN=$(aws apprunner list-services --region "$REGION" --query "ServiceSummaryList[?ServiceName=='salesapp-backend'].ServiceArn" --output text)
FRONTEND_ARN=$(aws apprunner list-services --region "$REGION" --query "ServiceSummaryList[?ServiceName=='salesapp-frontend'].ServiceArn" --output text)

if [ -n "$BACKEND_ARN" ]; then
    aws apprunner start-deployment --service-arn "$BACKEND_ARN" --region "$REGION" > /dev/null
    echo "    Backend deployment started."
fi

if [ -n "$FRONTEND_ARN" ]; then
    aws apprunner start-deployment --service-arn "$FRONTEND_ARN" --region "$REGION" > /dev/null
    echo "    Frontend deployment started."
fi

echo "==> Done. Monitor progress in the App Runner console."

read -r -p "Press Enter to close..."
