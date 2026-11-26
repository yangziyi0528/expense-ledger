# Project record

## 2026-11-26

- Assignment 1 scope set to the expense ledger. Keep the command-line interface and Gradle delivery from the brief. The student relayed an instructor change to Spring Boot. Use Java provisionally because the language was not specified separately.
- Use four feature branches for the domain model, H2 persistence, Spring Boot command line, and submission documentation. Each branch builds on the previous one so the repository can be reviewed in the same order as the implementation.
- Use decimal amounts with a two-place scale for CNY and store them in H2's fixed-point column. Keep month ordering and category totals in separate maps so grouping and presentation can be tested independently from storage.
- The first test worker could not load classes from the local Chinese path. Set the Gradle daemon to use the JVM's native encoding and retain UTF-8 Java source compilation. The subsequent local build passed 16 tests and found no PMD issues.
- Prepare the Markdown report and local evidence for a candidate. Record the remote URL, merged pull requests, and the final tag once those exist.
- Codex assisted with this implementation and the initial project documentation. Record later work here on the dates it happens.
- The instructor confirmed in chat that students could choose the language. The student selected Java with Spring Boot for Assignment 1.
- The first three pull requests were checked in dependency order and merged into main. [1. Model expenses and monthly totals](https://github.com/yangziyi0528/expense-ledger/pull/1), [2. Persist and filter expenses with embedded H2](https://github.com/yangziyi0528/expense-ledger/pull/2), [3. Package the Spring Boot command-line application](https://github.com/yangziyi0528/expense-ledger/pull/3).
- Clean builds and PMD passed for the domain and persistence branches. The full application passed 16 automated tests and 13 separate packaged CLI checks.
