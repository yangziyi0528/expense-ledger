package edu.nuistech.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExpenseTest {
    private static final LocalDate DATE = LocalDate.of(2026, 11, 26);

    @Test
    void normalizesCategoryAndStoresCurrencyAtTwoDecimalPlaces() {
        var expense = new Expense(DATE, "  FOOD ", new BigDecimal("8.5"), "  Lunch ");
        assertEquals("food", expense.category());
        assertEquals(new BigDecimal("8.50"), expense.amount());
        assertEquals("Lunch", expense.note());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "2.001", "10000000000"})
    void rejectsUnsupportedAmounts(String amount) {
        assertThrows(IllegalArgumentException.class,
                () -> new Expense(DATE, "food", new BigDecimal(amount), ""));
    }

    @Test
    void rejectsBlankCategoriesAndMultilineNotes() {
        var amount = new BigDecimal("1.00");
        assertThrows(IllegalArgumentException.class, () -> new Expense(DATE, " ", amount, ""));
        assertThrows(IllegalArgumentException.class,
                () -> new Expense(DATE, "food", amount, "first\nsecond"));
    }

    @Test
    void rejectsYearsOutsideTheDateFormat() {
        var amount = new BigDecimal("1.00");
        assertThrows(IllegalArgumentException.class,
                () -> new Expense(LocalDate.of(0, 1, 1), "food", amount, ""));
    }
}
