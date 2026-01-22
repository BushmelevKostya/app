# CI/CD Pipeline Documentation

## Overview

This project uses GitHub Actions for continuous integration and deployment. The pipeline consists of multiple workflows that ensure code quality, security, and automated testing.

## Workflows

### 1. CI/CD Pipeline (`ci-cd.yml`)

**Triggers:**
- Push to branches: `main`, `develop`, `refactoring`, `refactoring-step-*`
- Pull requests to: `main`, `develop`, `refactoring`

**Jobs:**

#### Build Backend
- Sets up JDK 17
- Builds the project with Gradle
- Uploads JAR artifacts (retained for 7 days)

#### Build Frontend
- Sets up Node.js 20
- Installs dependencies with `npm ci`
- Builds Angular application
- Uploads dist artifacts (retained for 7 days)

#### Test Backend
- Runs with PostgreSQL 16 and Redis containers
- Executes unit tests
- Generates JaCoCo coverage report
- Uploads test results and coverage reports

#### Security Check
- Runs OWASP Dependency Check
- Analyzes project dependencies for known vulnerabilities
- Uploads security report (retained for 30 days)

#### Code Quality
- Runs SpotBugs static analysis
- Detects potential bugs and code smells
- Uploads analysis reports

#### Docker Build
- Builds Docker image (only on main/develop branches)
- Uses BuildKit caching for faster builds
- Tests the built image

### 2. Pull Request Checks (`pr-checks.yml`)

**Triggers:**
- Pull request opened, synchronized, or reopened

**Jobs:**

#### PR Validation
- Validates PR title follows conventional commits format
- Checks for merge conflict markers

#### Lint Java Code
- Runs SpotBugs analysis
- Comments results on PR

#### Lint Frontend Code
- Runs ESLint on TypeScript/Angular code

#### Size Check
- Checks JAR file size
- Warns if build exceeds 200MB

### 3. Security Scan (`security-scan.yml`)

**Triggers:**
- Scheduled: Every Monday at 00:00 UTC
- Manual dispatch via GitHub UI

**Jobs:**

#### OWASP Dependency Check
- Scans for vulnerabilities in dependencies
- Fails if critical vulnerabilities found
- Reports retained for 90 days

#### Trivy Container Scan
- Scans Docker image for vulnerabilities
- Uploads results to GitHub Security tab
- Checks for CRITICAL and HIGH severity issues

#### Secret Scanning
- Uses Gitleaks to detect secrets in code
- Scans entire git history

## Configuration

### JaCoCo Coverage

Minimum coverage threshold: **60%**

Excluded from coverage:
- Configuration classes (`**/config/**`)
- DTOs (`**/dto/**`)
- Entities (`**/entity/**`)
- Application entry point

### SpotBugs

- Effort level: `max`
- Report level: `medium`
- Failures don't break the build (set to warning)

### OWASP Dependency Check

- Fails build on CVSS score ≥ 7
- Formats: HTML and JSON
- Suppressions file: `dependency-check-suppressions.xml`

## Local Testing

### Run tests with coverage:
```bash
./gradlew test jacocoTestReport
```

Coverage report: `build/reports/jacoco/test/html/index.html`

### Run security checks:
```bash
./gradlew dependencyCheckAnalyze
```

Security report: `build/reports/dependency-check-report.html`

### Run static analysis:
```bash
./gradlew spotbugsMain spotbugsTest
```

SpotBugs reports: `build/reports/spotbugs/`

## Artifacts

All workflows upload artifacts that can be downloaded from the Actions tab:

- **backend-build**: Compiled JAR files
- **frontend-build**: Built Angular application
- **test-results**: JUnit test reports
- **coverage-report**: JaCoCo coverage analysis
- **security-report**: OWASP dependency check
- **spotbugs-report**: Static analysis results

## Branch Strategy

- `main`: Production-ready code
- `develop`: Development integration branch
- `refactoring`: Refactoring work
- `refactoring-step-*`: Individual refactoring steps

## Conventional Commits

PR titles must follow the format:

```
type(scope): description
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks
- `perf`: Performance improvements
- `ci`: CI/CD changes
- `build`: Build system changes
- `revert`: Reverting changes

**Example:**
```
feat(auth): add JWT token refresh mechanism
fix(movie): resolve null pointer in director check
docs(readme): update installation instructions
```

## Troubleshooting

### Tests failing in CI but passing locally

1. Check database/Redis connection settings
2. Verify environment variables in workflow
3. Review test isolation - tests may depend on order

### Security scan false positives

Add suppressions to `dependency-check-suppressions.xml`:

```xml
<suppress>
    <notes>Explanation why this is a false positive</notes>
    <packageUrl regex="true">^pkg:maven/group/artifact@.*$</packageUrl>
    <cve>CVE-XXXX-XXXXX</cve>
</suppress>
```

### Build size warnings

If JAR exceeds 200MB:
- Review dependencies for unnecessary libraries
- Consider excluding unused transitive dependencies
- Check for embedded resources that could be external

## Future Improvements

- [ ] Add integration tests stage
- [ ] Deploy to staging environment
- [ ] Performance testing
- [ ] Contract testing for APIs
- [ ] Automated dependency updates (Dependabot)
- [ ] Release automation
