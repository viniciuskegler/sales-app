#!/bin/bash
# Full deploy: provisions all AWS infrastructure, builds and pushes the Docker
# image to ECR, then deploys the App Runner service.
# Requires AWS CLI and Docker to be running.
set -e
trap 'echo; read -r -p "Script failed. Press Enter to close..."' ERR

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

export CDK_DEFAULT_ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
export CDK_DEFAULT_REGION=$(aws configure get region)

echo "==> Deploying InfraStack (ECR, RDS, Redis, SQS, VPC)..."
cd "$ROOT_DIR/infra"
cdk deploy InfraStack --require-approval never

echo "==> Deploying AppStack (App Runner)..."
cdk deploy AppStack --require-approval never

echo "==> Building and pushing Docker image..."
cd "$ROOT_DIR"
./publish.sh

echo ""
echo "==> Done. App Runner will pick up the new image automatically."
read -r -p "Press Enter to close..."
