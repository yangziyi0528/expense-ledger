package edu.nuistech.ledger;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.ScopeType;

@Component
final class LedgerCli {
    private final LedgerStore store;

    LedgerCli(LedgerStore store) {
        this.store = store;
    }

    int run(String[] args, PrintWriter out, PrintWriter err) {
        var cli = new CommandLine(new Root(store, out));
        cli.setOut(out);
        cli.setErr(err);
        cli.setColorScheme(CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.OFF));
        cli.registerConverter(YearMonth.class, value -> {
            YearMonth month = YearMonth.parse(value);
            Expense.checkYear(month.getYear());
            return month;
        });
        cli.setExecutionExceptionHandler((failure, command, result) -> {
            // Invalid requests and storage failures have different exit statuses for scripts.
            if (failure instanceof IllegalArgumentException) {
                command.getErr().println("Error: " + failure.getMessage());
                return CommandLine.ExitCode.USAGE;
            }
            if (failure instanceof LedgerStore.StorageException) {
                command.getErr().println("Error: " + failure.getMessage());
                return CommandLine.ExitCode.SOFTWARE;
            }
            throw failure;
        });
        return cli.execute(args);
    }

    @Command(name = "expense-ledger", mixinStandardHelpOptions = true, version = "Expense Ledger 0.1.0",
            description = "Record expenses and report monthly totals in CNY.",
            subcommands = {Add.class, ListExpenses.class, Summary.class})
    static final class Root {
        private final LedgerStore store;
        private final PrintWriter out;

        @Option(names = "--data-dir", scope = ScopeType.INHERIT, paramLabel = "DIR",
                description = "Ledger directory (default: ledger-data).")
        private Path directory = Path.of("ledger-data");

        Root(LedgerStore store, PrintWriter out) {
            this.store = store;
            this.out = out;
        }
    }

    @Command(name = "add", mixinStandardHelpOptions = true, description = "Record an expense.")
    static final class Add implements Runnable {
        @ParentCommand
        private Root root;

        @Option(names = "--date", required = true, paramLabel = "YYYY-MM-DD")
        private LocalDate date;

        @Option(names = "--category", required = true, paramLabel = "CATEGORY")
        private String category;

        @Option(names = "--amount", required = true, paramLabel = "CNY")
        private BigDecimal amount;

        @Option(names = "--note", defaultValue = "", paramLabel = "TEXT")
        private String note;

        @Override
        public void run() {
            root.store.add(root.directory, new Expense(date, category, amount, note));
            root.out.println("Expense recorded.");
        }
    }

    @Command(name = "list", mixinStandardHelpOptions = true, description = "List recorded expenses.")
    static final class ListExpenses implements Runnable {
        @ParentCommand
        private Root root;

        @Option(names = "--month", paramLabel = "YYYY-MM")
        private YearMonth month;

        @Option(names = "--category", paramLabel = "CATEGORY")
        private String category;

        @Override
        public void run() {
            var entries = root.store.list(root.directory, month, category);
            if (entries.isEmpty()) {
                root.out.println("No matching expenses.");
                return;
            }
            root.out.println("ID\tDATE\tCATEGORY\tAMOUNT (CNY)\tNOTE");
            for (var entry : entries) {
                Expense expense = entry.expense();
                root.out.printf("%d\t%s\t%s\t%s\t%s%n", entry.id(), expense.date(),
                        expense.category(), expense.amount().toPlainString(), expense.note());
            }
        }
    }

    @Command(name = "summary", mixinStandardHelpOptions = true, description = "Show monthly and category totals.")
    static final class Summary implements Runnable {
        @ParentCommand
        private Root root;

        @Option(names = "--month", paramLabel = "YYYY-MM")
        private YearMonth month;

        @Override
        public void run() {
            var expenses = root.store.list(root.directory, month, null).stream()
                    .map(LedgerStore.Entry::expense).toList();
            var report = LedgerReport.summarize(expenses);
            if (report.isEmpty()) {
                root.out.println(month == null ? "No expenses to summarize." : month + " total: CNY 0.00");
                return;
            }
            for (var row : report) {
                root.out.printf("%s total: CNY %s%n", row.month(), row.total().toPlainString());
                row.categories().entrySet().stream().sorted(java.util.Map.Entry.comparingByKey())
                        .forEach(category -> root.out.printf("  %s: CNY %s%n",
                                category.getKey(), category.getValue().toPlainString()));
            }
        }
    }
}
