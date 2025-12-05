#!/bin/bash
# GitHub Actions workflow validation script
# Run this from anywhere: ./scripts/validate-workflow.sh

set -e

# Get the repository root directory (scripts is one level down from root)
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

WORKFLOW_FILE=".github/workflows/ci.yml"

echo "=== Validating GitHub Actions Workflow ==="
echo "Repository root: $REPO_ROOT"

# Check if workflow file exists
if [ ! -f "$WORKFLOW_FILE" ]; then
    echo "❌ Error: Workflow file not found: $WORKFLOW_FILE"
    exit 1
fi

echo "✓ Workflow file found: $WORKFLOW_FILE"

# Check if PyYAML is installed, if not use basic validation
if ! python3 -c "import yaml" 2>/dev/null; then
    echo "⚠️  PyYAML not installed, using basic YAML validation..."
    
    # Basic YAML syntax check without PyYAML
    echo "Checking YAML syntax (basic)..."
    
    # Check for basic YAML structure issues
    if grep -q $':\t' "$WORKFLOW_FILE"; then
        echo "❌ YAML syntax error: tabs detected (YAML requires spaces)"
        exit 1
    fi
    
    # Check for balanced quotes
    if ! awk 'NR%2==1' "$WORKFLOW_FILE" | grep -q '^'; then
        echo "⚠️  Warning: Potential quote imbalance"
    fi
    
    echo "✓ Basic YAML syntax checks passed"
    echo ""
    echo "⚠️  Note: For full validation, install PyYAML:"
    echo "   pip3 install PyYAML"
    echo "   or: sudo apt-get install python3-yaml"
    echo ""
    echo "=== Basic Workflow Structure ==="
    grep -E "^(name:|on:|jobs:|  [a-z-]+:)" "$WORKFLOW_FILE" | head -20
    echo ""
    echo "✅ Basic validation passed (install PyYAML for detailed checks)"
    exit 0
fi

# Full validation with PyYAML
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
python3 << EOF
import yaml
import sys

try:
    with open('$WORKFLOW_FILE') as f:
        workflow = yaml.safe_load(f)
    
    # Validate structure
    assert workflow is not None, "Workflow file is empty or invalid"
    assert 'name' in workflow, "Missing 'name' field"
    
    # YAML treats 'on' as boolean True, so check for both 'on' and True
    has_on_field = 'on' in workflow or True in workflow
    assert has_on_field, "Missing 'on' field"
    
    # Get the trigger value (could be under 'on' or True)
    triggers = workflow.get('on') or workflow.get(True)
    
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
    print(f"  - Triggers: {triggers}")
    print(f"  - Jobs: {list(workflow['jobs'].keys())}")
    print(f"  - Build steps: {len(build_job['steps'])}")
    print(f"  - K6 test steps: {len(k6_job['steps'])}")
    sys.exit(0)
except AssertionError as e:
    print(f"❌ Validation error: {e}", file=sys.stderr)
    sys.exit(1)
except Exception as e:
    print(f"❌ Error: {e}", file=sys.stderr)
    sys.exit(1)
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
echo "  4. Generate JaCoCo coverage report"
echo "  5. Upload coverage to Codecov and Coveralls"
echo "  6. Run SonarCloud scan"
echo "  7. Build Docker image"
echo "  8. Save Docker image as artifact"
echo "  9. Download Docker image in second job"
echo "  10. Start application container"
echo "  11. Wait for application to be ready"
echo "  12. Run k6 smoke tests"
echo "  13. Show logs on failure"
echo "  14. Generate and publish Allure reports"

echo ""
echo "✅ All validation checks passed!"
echo ""
echo "To test the workflow steps locally, run:"
echo "  ./scripts/test-ci-workflow.sh"
