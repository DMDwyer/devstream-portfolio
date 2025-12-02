#!/bin/bash
set -e

echo "=== Testing CI Workflow Steps ==="

# Step 1: Build with Maven
echo -e "\n[1/5] Running Maven build..."
./mvnw -B verify -q

# Step 2: Build Docker image
echo -e "\n[2/5] Building Docker image..."
docker build -t devstream-portfolio:ci -f infra/docker/Dockerfile . > /dev/null

# Step 3: Save and load Docker image (simulating artifact transfer)
echo -e "\n[3/5] Simulating artifact upload/download..."
docker save devstream-portfolio:ci > /tmp/app-image-test.tar
docker rmi devstream-portfolio:ci
docker load < /tmp/app-image-test.tar > /dev/null
rm /tmp/app-image-test.tar

# Step 4: Start application container
echo -e "\n[4/5] Starting application container..."
docker run -d --name app-test -p 8081:8080 devstream-portfolio:ci > /dev/null

# Wait for app to be ready
echo "Waiting for application to start..."
for i in {1..30}; do
    if curl -f http://localhost:8081/flags > /dev/null 2>&1; then
        echo "✓ Application is ready"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "✗ Application failed to start"
        docker logs app-test
        docker stop app-test > /dev/null 2>&1
        docker rm app-test > /dev/null 2>&1
        exit 1
    fi
    sleep 2
done

# Step 5: Run k6 smoke tests
echo -e "\n[5/5] Running k6 smoke tests..."
docker run --rm --network host -v "$PWD/src/test/k6:/scripts" grafana/k6 run -e BASE_URL=http://localhost:8081 /scripts/smoke.js

# Cleanup
echo -e "\n=== Cleaning up ==="
docker stop app-test > /dev/null
docker rm app-test > /dev/null
docker rmi devstream-portfolio:ci > /dev/null

echo -e "\n✓ All CI workflow steps passed successfully!"
