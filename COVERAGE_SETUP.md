# Coverage and Code Quality Setup

This document describes the required secrets for coverage and code quality integrations.

## Required GitHub Secrets

### CODECOV_TOKEN
1. Go to [codecov.io](https://codecov.io)
2. Sign in with GitHub
3. Add repository: `DMDwyer/devstream-portfolio`
4. Copy the repository token
5. Add to GitHub: Settings → Secrets → Actions → New repository secret
   - Name: `CODECOV_TOKEN`
   - Value: [paste token]

### SONAR_TOKEN
1. Go to [sonarcloud.io](https://sonarcloud.io)
2. Sign in with GitHub
3. Click "+" → Analyze new project
4. Select `devstream-portfolio`
5. Follow setup wizard
6. Go to Account → Security → Generate token
7. Add to GitHub: Settings → Secrets → Actions → New repository secret
   - Name: `SONAR_TOKEN`
   - Value: [paste token]

### COVERALLS (Optional - uses GITHUB_TOKEN by default)
Coveralls automatically uses the `GITHUB_TOKEN` provided by GitHub Actions, no additional secret needed.

## Verification

After setting up secrets:

1. Push to trigger CI workflow
2. Check Actions tab for successful coverage uploads
3. Visit dashboards:
   - Codecov: https://codecov.io/gh/DMDwyer/devstream-portfolio
   - SonarCloud: https://sonarcloud.io/project/overview?id=DMDwyer_devstream-portfolio
   - Coveralls: https://coveralls.io/github/DMDwyer/devstream-portfolio

## Local Testing

Test coverage locally without secrets:
```bash
./mvnw clean test jacoco:report
open target/site/jacoco/index.html
```
