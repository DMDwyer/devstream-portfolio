#!/bin/bash
# GitHub Actions workflow validation script

set -e

WORKFLOW_FILE=".github/workflows/ci.yml"

echo "=== Validating GitHub Actions Workflow ==="

# Check if workflow file exists
if [ ! -f "$WORKFLOW_FILE" ]; then
    echo "❌ Error: Workflow file not found: $WORKFLOW_FILE"
    exit 1
fi

echo "✓ Workflow file found: $WORKFLOW_FILE"

# Validate YAML syntax using Python
echo "Checking YAML syntax..."
python3 -c "import yaml, sys; yaml.safe_load(open('$WORKFLOW_FILE'))" 2>&1
if [ $? -eq 0 ]; then
    echo "✓ YAML syntax is valid"
else
    echo "❌ YAML syntax error detected"
    exit 1
fi

# Check for required keys
echo "Checking workflow structure..."
python3 << 'EOF'
import yaml
with open('.github/workflows/ci.yml') as f:
    workflow = yaml.safe_load(f)
    
assert 'name' in workflow, "Missing 'name' field"
assert 'on' in workflow, "Missing 'on' field"
assert 'jobs' in workflow, "Missing 'jobs' field"
assert 'build-and-test' in workflow['jobs'], "Missing 'build-and-test' job"
assert 'k6-smoke-test' in workflow['jobs'], "Missing 'k6-smoke-test' job"

# Check build-and-test job
build_job = workflow['jobs']['build-and-test']
assert 'runs-on' in build_job, "build-and-test missing 'runs-on'"
assert 'steps' in build_job, "build-and-test missing 'steps'"

# Check k6-smoke-test job
k6_job = workflow['jobs']['k6-smoke-test']
assert 'needs' in k6_job, "k6-smoke-test missing 'needs'"
assert k6_job['needs'] == 'build-and-test', "k6-smoke-test 'needs' should be 'build-and-test'"

print("✓ Workflow structure is valid")
print(f"  - Name: {workflow['name']}")
print(f"  - Triggers: {workflow['on']}")
print(f"  - Jobs: {list(workflow['jobs'].keys())}")
print(f"  - Build steps: {len(build_job['steps'])}")
print(f"  - K6 test steps: {len(k6_job['steps'])}")
EOF

if [ $? -ne 0 ]; then
    echo "❌ Workflow structure validation failed"
    exit 1
fi

echo ""
echo "=== Workflow Summary ==="
echo "The workflow will:"
echo "  1. Checkout code"
echo "  2. Set up JDK 21"
echo "  3. Run Maven build and tests (./mvnw -B verify)"
echo "  4. Build Docker image"
echo "  5. Save Docker image as artifact"
echo "  6. Download Docker image in second job"
echo "  7. Start application container"
echo "  8. Wait for application to be ready"
echo "  9. Run k6 smoke tests"
echo "  10. Show logs on failure"

echo ""
echo "✅ All validation checks passed!"
echo ""
echo "To test the workflow steps locally, run:"
echo "  ./test-ci-workflow.sh"
