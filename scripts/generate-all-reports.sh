#!/bin/bash
set -e

echo "🧪 Running tests with coverage and generating reports..."

# Run tests with coverage
./mvnw clean test jacoco:report

# Generate Allure report
./mvnw allure:report

# Copy JaCoCo report to Allure report directory for unified view
mkdir -p target/site/allure-maven-plugin/jacoco
cp -r target/site/jacoco/* target/site/allure-maven-plugin/jacoco/ 2>/dev/null || true

echo ""
echo "✅ Reports generated successfully!"
echo "📊 Coverage report: target/site/jacoco/index.html"
echo "📋 Allure report: target/site/allure-maven-plugin/index.html"
echo ""

# Try to open reports in browser
if command -v xdg-open &> /dev/null; then
    xdg-open target/site/jacoco/index.html
    xdg-open target/site/allure-maven-plugin/index.html
elif command -v open &> /dev/null; then
    open target/site/jacoco/index.html
    open target/site/allure-maven-plugin/index.html
else
    echo "💡 Manually open the reports in your browser"
fi
