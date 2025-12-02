# Contributing to Devstream Portfolio

Thank you for considering contributing to this project! This document outlines the standards and practices for contributing.

## 🌿 Branch Naming Convention

Use the following prefixes for branch names:

- `feat-` - New features or enhancements
  - Example: `feat-add-user-authentication`
- `fix-` - Bug fixes
  - Example: `fix-null-pointer-in-flag-service`
- `docs-` - Documentation updates
  - Example: `docs-update-api-guide`
- `test-` - Adding or updating tests
  - Example: `test-add-mapper-unit-tests`
- `refactor-` - Code refactoring without changing functionality
  - Example: `refactor-simplify-flag-evaluation`
- `chore-` - Maintenance tasks, dependency updates
  - Example: `chore-upgrade-spring-boot`
- `ci-` - CI/CD pipeline changes
  - Example: `ci-add-allure-reporting`

**Branch name format:** `<type>-<brief-description-in-kebab-case>`

## 📝 Commit Message Format

We follow [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, missing semicolons, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks
- `ci`: CI/CD changes
- `perf`: Performance improvements
- `build`: Build system or dependency changes

### Examples

```
feat: add flag evaluation endpoint with attribute-based rules

Implements GET /flags/{key}/evaluate endpoint that supports
user attributes for rule-based variant selection.

Closes #42
```

```
fix: prevent null pointer in FlagMapper when DTO is null

Added null checks in updateEntity method to handle cases where
patch DTO contains null values for required fields.
```

```
test: add Allure annotations to all JUnit tests

- Annotate tests with @Epic, @Feature, @Story
- Add severity levels (BLOCKER, CRITICAL, NORMAL)
- Include detailed descriptions for better reporting
```

### Commit Message Rules

- Use imperative mood ("add" not "added" or "adds")
- Don't capitalize first letter
- No period (.) at the end of the subject line
- Limit subject line to 72 characters
- Separate subject from body with a blank line
- Wrap body at 72 characters
- Use body to explain *what* and *why* vs *how*

## 🔄 Pull Request Process

1. **Create a feature branch** from `main`
   ```bash
   git checkout -b feat-your-feature-name
   ```

2. **Make your changes** following the coding standards below

3. **Write or update tests** for your changes
   - Unit tests for new logic
   - Integration tests for API changes
   - Add Allure annotations for test organization

4. **Run tests locally** before pushing
   ```bash
   ./mvnw test
   ./mvnw verify
   ```

5. **Commit your changes** using conventional commits

6. **Push to your branch**
   ```bash
   git push origin feat-your-feature-name
   ```

7. **Open a Pull Request** with:
   - Clear title following commit message format
   - Description explaining what and why
   - Link to related issues
   - Screenshots/examples if UI changes
   - Checklist of completed items

### Pull Request Template

```markdown
## Description
Brief description of what this PR does

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Changes Made
- Change 1
- Change 2

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] All tests passing locally
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Allure annotations added to new tests

Closes #(issue)
```

## 🧪 Testing Standards

### Test Organization

Tests should be organized using Allure annotations:

```java
@Epic("Feature Flags Management")
@Feature("Flag Controller API")
public class FlagControllerTest {
    
    @Test
    @Story("Create and retrieve feature flags")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Integration test that creates a flag and verifies retrieval")
    public void testCreateAndRetrieve() {
        // test implementation
    }
}
```

### Test Naming

- Use descriptive method names: `methodName_whenCondition_thenExpectedBehavior()`
- Example: `updateEntity_withNullEnabled_doesNotOverwrite()`

### Coverage Requirements

- Aim for 80%+ code coverage
- All public methods should have unit tests
- Integration tests for all API endpoints
- Edge cases and error conditions must be tested

## 💻 Coding Standards

### Java

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use Java 21 features where appropriate
- Prefer records for DTOs
- Use `var` for local variables when type is obvious
- Keep methods small and focused (< 20 lines when possible)

### Code Quality

- No commented-out code in commits
- Remove unused imports
- Use meaningful variable names
- Add JavaDoc for public APIs
- Keep classes focused (Single Responsibility Principle)

### MapStruct

- Use `@Mapper(componentModel = "spring")` for Spring integration
- Configure null handling strategies explicitly
- Document custom mapping logic

## 📚 Documentation

- Update README.md for user-facing changes
- Add JavaDoc for public APIs
- Include code examples for complex features
- Update API documentation (Swagger/OpenAPI)
- Document breaking changes in PR description

## 🔍 Code Review

Reviewers should check:

- [ ] Code follows project conventions
- [ ] Tests are comprehensive and pass
- [ ] No security vulnerabilities introduced
- [ ] Performance impact is acceptable
- [ ] Documentation is updated
- [ ] Commit messages follow conventions
- [ ] No unnecessary dependencies added

## 🚀 Release Process

1. Ensure all tests pass
2. Update version in `pom.xml`
3. Update CHANGELOG.md
4. Create release PR to `main`
5. Tag release after merge: `git tag -a v1.0.0 -m "Release v1.0.0"`
6. Push tags: `git push origin --tags`

## ❓ Questions?

If you have questions about contributing, please:
- Check existing issues and PRs
- Review the README and documentation
- Open a discussion in GitHub Discussions

## 📜 License

By contributing, you agree that your contributions will be licensed under the same license as the project.
