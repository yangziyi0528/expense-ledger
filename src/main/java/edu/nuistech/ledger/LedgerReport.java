package edu.nuistech.ledger;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

final class LedgerReport {
    record MonthTotals(YearMonth month, BigDecimal total, Map<String, BigDecimal> categories) {}

    private LedgerReport() {
    }

    static List<MonthTotals> summarize(Iterable<Expense> expenses) {
        var months = new TreeMap<YearMonth, Map<String, BigDecimal>>();
        for (Expense expense : expenses) {
            YearMonth month = YearMonth.from(expense.date());
            months.computeIfAbsent(month, key -> new HashMap<>())
                    .merge(expense.category(), expense.amount(), BigDecimal::add);
        }

        var report = new ArrayList<MonthTotals>();
        months.forEach((month, categories) -> {
            BigDecimal total = categories.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            report.add(new MonthTotals(month, total, Map.copyOf(categories)));
        });
        return List.copyOf(report);
    }
}
