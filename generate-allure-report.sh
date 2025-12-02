#!/bin/bash
set -e

echo "=== Generating Allure Report ==="

# Check if allure-results exists
if [ ! -d "allure-results" ]; then
    echo "❌ No allure-results directory found. Run tests first with: ./mvnw test"
    exit 1
fi

# Count result files
RESULT_COUNT=$(ls -1 allure-results/*.json 2>/dev/null | wc -l)
echo "Found $RESULT_COUNT Allure result files"

if [ $RESULT_COUNT -eq 0 ]; then
    echo "❌ No test results found. Run tests first with: ./mvnw test"
    exit 1
fi

# Generate the report
echo "Generating Allure report..."
./mvnw allure:report

# Check if report was generated
if [ -d "target/site/allure-maven-plugin" ]; then
    echo "✓ Allure report generated successfully"
    echo ""
    echo "Report location: target/site/allure-maven-plugin/index.html"
    echo ""
    echo "To view the report:"
    echo "  ./mvnw allure:serve"
    echo ""
    echo "Or open manually:"
    echo "  xdg-open target/site/allure-maven-plugin/index.html"
else
    echo "❌ Report generation failed"
    exit 1
fi
