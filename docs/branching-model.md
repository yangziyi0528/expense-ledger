# Branching model

`main` is the integration branch. The initial local branches are stacked because each increment uses the code added by the preceding one. Publish the baseline `main` and the four feature branches when the assignment repository is available. Open the pull requests in the order below, using the prior feature branch as the base for a stacked preview or waiting for its merge into `main` before opening the next PR.

| Order | Branch | Review focus |
| --- | --- | --- |
| 1 | `feature/domain` | Expense validation, decimal arithmetic, grouping, and domain tests. |
| 2 | `feature/persistence` | Embedded H2, date and category filtering, and storage tests. |
| 3 | `feature/command-line` | Spring Boot entry point, Picocli, exit statuses, and the packaged build. |
| 4 | `feature/submission-docs` | Build instructions, project record, and report source. |

Keep the original commits when merging the stacked branches. A merge commit preserves their ancestry. Review each PR against `main` after its predecessor is merged. Before the application entry point is present, run `./gradlew test pmdMain pmdTest` for code changes. From branch 3 onward, run `./gradlew clean build`. On Windows, use `.\gradlew.bat` for the same tasks and use an ASCII checkout path when testing the first two branches.

Once all four PRs are merged, build a fresh clone of `main`, record the result and PR links in the project record, and tag the reviewed commit for submission. The remote repository URL, the submission tag, and the separately exported PDF are the three submission items. Keep build outputs, local ledger files, and credentials out of Git.
