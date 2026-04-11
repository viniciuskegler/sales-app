#!/bin/bash
# Full deploy: provisions all AWS infrastructure, builds and pushes Docker
# images to ECR, then deploys both App Runner services.
# Requires AWS CLI and Docker to be running.
set -e
trap 'echo; read -r -p "Script failed. Press Enter to close..."' ERR

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

export CDK_DEFAULT_ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
export CDK_DEFAULT_REGION=$(aws configure get region)
export AWS_DEFAULT_REGION="$CDK_DEFAULT_REGION"

echo "==> Deploying InfraStack (ECR, RDS, Redis, SQS, VPC)..."
cd "$ROOT_DIR/infra"
cdk deploy InfraStack --require-approval never

echo "==> Building and pushing Docker images..."
cd "$ROOT_DIR"
./publish.sh

echo "==> Deploying AppStack (App Runner)..."
cd "$ROOT_DIR/infra"
cdk deploy AppStack --require-approval never

echo ""
echo "==> Done."
read -r -p "Press Enter to close..."
