#!/bin/bash
set -e

echo "🧪 Running tests and generating coverage report..."
./mvnw clean test jacoco:report

echo ""
echo "✅ Coverage report generated!"
echo "📊 Open: target/site/jacoco/index.html"
echo ""

# Open report in browser if available
if command -v xdg-open &> /dev/null; then
    xdg-open target/site/jacoco/index.html
elif command -v open &> /dev/null; then
    open target/site/jacoco/index.html
else
    echo "💡 Manually open target/site/jacoco/index.html in your browser"
fi
