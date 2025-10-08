# Selenium — Separation of Concerns (SoC) Template

**Goal:** demonstrate a clean test architecture where each layer has one responsibility.

```
core/        -> drivers, config, wait helpers
utils/       -> common helpers (randoms, strings, timers)
assertions/  -> custom assertions facade (AssertJ-friendly)
pages/       -> page objects (ONLY locators + small actions)
pages/components/ -> reusable widgets/components
flows/       -> business flows (multi-step user journeys)
data/        -> test data builders / fixtures
tests/       -> HIGH-LEVEL specs only (call flows + assertions)
```

### Quick start
```bash
mvn -Denv=local -Dbrowser=chrome clean test
```
Defaults are taken from `src/test/resources/env/local.properties`.

### Why this is SoC
- Tests never talk to WebDriver or locators directly.
- Pages expose tiny actions (click, type) and read-only state.
- Flows orchestrate pages for business outcomes.
- Assertions are centralized; swap frameworks easily.
- Config is injected into layers (no hard-coded URLs).
