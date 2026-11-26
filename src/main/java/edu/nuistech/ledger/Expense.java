package edu.nuistech.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

record Expense(LocalDate date, String category, BigDecimal amount, String note) {
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");

    Expense {
        checkYear(date.getYear());
        category = normalizeCategory(category);
        if (amount.signum() <= 0 || amount.scale() > 2 || amount.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Amount must be positive, at most 9999999999.99, "
                    + "and have at most two decimal places.");
        }
        amount = amount.setScale(2);
        note = note.strip();
        checkText(note, "Note", 200);
    }

    static String normalizeCategory(String rawCategory) {
        String category = rawCategory.strip().toLowerCase(Locale.ROOT);
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Category must not be blank.");
        }
        checkText(category, "Category", 40);
        return category;
    }

    static void checkYear(int year) {
        if (year < 1 || year > 9999) {
            throw new IllegalArgumentException("Year must be between 0001 and 9999.");
        }
    }

    private static void checkText(String text, String label, int maxLength) {
        if (text.length() > maxLength || text.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException(label + " must fit in " + maxLength
                    + " characters and contain no control characters.");
        }
    }
}
