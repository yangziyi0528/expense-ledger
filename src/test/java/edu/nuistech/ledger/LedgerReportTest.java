package edu.nuistech.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LedgerReportTest {
    @Test
    void groupsCategoriesWithinMonthsAndSortsAcrossYears() {
        var result = LedgerReport.summarize(List.of(
                expense("2026-01-01", "food", "1.30"),
                expense("2025-12-31", "transport", "2.00"),
                expense("2026-01-02", "FOOD", "0.10"),
                expense("2026-01-03", "books", "3.05")));

        assertEquals(List.of(YearMonth.of(2025, 12), YearMonth.of(2026, 1)),
                result.stream().map(LedgerReport.MonthTotals::month).toList());
        assertEquals(new BigDecimal("2.00"), result.get(0).total());
        assertEquals(new BigDecimal("4.45"), result.get(1).total());
        assertEquals(Map.of("books", new BigDecimal("3.05"), "food", new BigDecimal("1.40")),
                result.get(1).categories());
    }

    @Test
    void emptyLedgerProducesNoMonthRows() {
        assertTrue(LedgerReport.summarize(List.of()).isEmpty());
    }

    private Expense expense(String date, String category, String amount) {
        return new Expense(LocalDate.parse(date), category, new BigDecimal(amount), "");
    }
}
