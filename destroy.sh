#!/bin/bash
# Tears down all AWS resources for the sales app.
# Destroys AppStack first, then InfraStack (order matters).
# ECR images are deleted automatically (emptyOnDelete=true on the repo).
set -e
trap 'echo; read -r -p "Script failed. Press Enter to close..."' ERR

export CDK_DEFAULT_ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
export CDK_DEFAULT_REGION=$(aws configure get region)

cd "$(dirname "$0")/infra"

echo "==> Destroying AppStack (App Runner)..."
cdk destroy AppStack --force

echo "==> Destroying InfraStack (ECR, RDS, Redis, SQS, VPC)..."
cdk destroy InfraStack --force

echo ""
echo "==> Done. All resources destroyed."
read -r -p "Press Enter to close..."
