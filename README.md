# Expense Ledger

Expense Ledger is a Spring Boot command-line application for recording expenses in CNY, finding entries by date and category, and calculating monthly and per-category totals. It stores each ledger in an embedded H2 database.

## Build and run

Use a JDK supported by Gradle 8.14.3 and Spring Boot 3.5, such as JDK 17, 21, or 24. The project compiles for Java 17. The first build needs network access to download Gradle and dependencies.

On Windows PowerShell:

```powershell
.\gradlew.bat clean build
```

On Linux or macOS:

```sh
./gradlew clean build
```

The build produces `build/libs/expense-ledger-0.1.0.jar`. The examples use a separate data directory so they leave the default ledger untouched.

```text
java -jar build/libs/expense-ledger-0.1.0.jar --help
java -jar build/libs/expense-ledger-0.1.0.jar --data-dir example-data add --date 2026-11-26 --category Food --amount 12.50 --note "Lunch"
java -jar build/libs/expense-ledger-0.1.0.jar --data-dir example-data list --month 2026-11 --category food
java -jar build/libs/expense-ledger-0.1.0.jar --data-dir example-data summary --month 2026-11
```

With no `--data-dir`, the database is `ledger-data/ledger.mv.db` relative to the current directory. Use the same data directory for commands working on the same ledger. Categories are trimmed and case-insensitive. Dates use `YYYY-MM-DD` and month filters use `YYYY-MM`. Amounts must be positive with at most two decimal places. A selected month with no expenses totals `CNY 0.00`. The exit codes are 0 on success, 2 for invalid input, and 1 for storage or application errors.

## Architecture

| Component | Responsibility |
| --- | --- |
| `LedgerApplication` | Starts the non-web Spring Boot context and passes the CLI status to the operating system. |
| `LedgerCli` | Defines the Picocli commands, converts arguments, and routes results and errors to the appropriate output streams. |
| `Expense` | Validates and normalizes a single expense. |
| `LedgerStore` | Creates the local table, binds SQL parameters, and persists or reads expenses. |
| `LedgerReport` | Uses a `TreeMap` to group expenses in month order and a `HashMap` per month to accumulate category subtotals. |

## Verification

`build` runs the JUnit tests and PMD. The JaCoCo report also runs after `test`. To run the tools explicitly, use `./gradlew test jacocoTestReport pmdMain pmdTest` or `.\gradlew.bat test jacocoTestReport pmdMain pmdTest`.

The local build passed all 16 tests. JaCoCo reported 91.2% instruction coverage, 91.3% branch coverage, and 90.1% line coverage. PMD reported zero findings for both the main and test source sets. No rules were suppressed.

| Output | Location |
| --- | --- |
| JUnit HTML | `build/reports/tests/test/index.html` |
| JaCoCo HTML | `build/reports/jacoco/test/html/index.html` |
| JaCoCo XML | `build/reports/jacoco/test/jacocoTestReport.xml` |
| PMD HTML and XML | `build/reports/pmd/` |

## Workflow

See the [branching model](docs/branching-model.md) and [project record](docs/project-record.md). The assignment report is supplied separately as a PDF. The Wrapper removes the need for a system Gradle installation. Dependency locking records the versions the build resolved. Running the tests and PMD from Gradle makes both checks repeatable from a fresh checkout. Coverage highlights paths that need manual verification, including the launch of the installed JAR.

The local assignment package puts the exported PDF in `output/report/` and its verification figures in `output/evidence/`. Gradle regenerates the executable JAR and tool reports in `build/`.

On Windows, the Gradle daemon uses the JVM's native encoding for worker argument files and still compiles Java sources as UTF-8. If the system code page cannot represent the local checkout path, use a path containing ASCII characters to build. [Gradle worker encoding issue](https://github.com/gradle/gradle/issues/30304)

## References and assistance

- [Assignment 1, Software Development Tools Continuous Assessment Brief, 2026–2027] was provided by the course.
- [Spring Boot 3.5 documentation](https://docs.spring.io/spring-boot/3.5/)
- [Picocli user manual](https://picocli.info/)
- [H2 documentation and source](https://github.com/h2database/h2database)
- [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html), [dependency locking](https://docs.gradle.org/current/userguide/dependency_locking.html), [JaCoCo](https://docs.gradle.org/current/userguide/jacoco_plugin.html), and [PMD](https://docs.gradle.org/current/userguide/pmd_plugin.html)
- [JUnit 5 user guide](https://docs.junit.org/5.12.2/user-guide/)

Codex assisted with the initial code, tests, build configuration, and documentation. The separate assignment report contains the verification evidence.
