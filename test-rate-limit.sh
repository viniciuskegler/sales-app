#!/bin/bash
URL="https://iumzw5gsng.us-east-1.awsapprunner.com/api/products/1"
TOTAL=70

echo "==> Sending $TOTAL requests to $URL"
echo ""

for i in $(seq 1 $TOTAL); do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$URL")
    echo "Request $i — HTTP $STATUS"
    if [ "$STATUS" = "429" ]; then
        echo "==> Rate limit hit on request $i"
    fi
done

echo ""
echo "==> Done."
read -r -p "Press Enter to close..."
