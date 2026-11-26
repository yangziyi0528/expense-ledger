package edu.nuistech.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LedgerStoreTest {
    @TempDir
    Path directory;

    @Test
    void savesRowsForLaterInvocationsAndFiltersByMonthAndCategory() {
        var writer = new LedgerStore();
        var leapDay = expense("2024-02-29", "food");
        writer.add(directory, leapDay);
        writer.add(directory, expense("2024-02-01", "books"));
        writer.add(directory, expense("2024-03-01", "food"));

        var reader = new LedgerStore();
        var matches = reader.list(directory, YearMonth.of(2024, 2), " FOOD ");
        assertEquals(1, matches.size());
        assertEquals(leapDay, matches.get(0).expense());
        assertTrue(matches.get(0).id() > 0);
        assertEquals("books", reader.list(directory, null, null).get(0).expense().category());
        assertTrue(reader.list(directory, YearMonth.of(2024, 1), null).isEmpty());
    }

    @Test
    void reportsAFileThatBlocksTheDataDirectory() throws java.io.IOException {
        Path file = Files.createFile(directory.resolve("blocked"));
        assertThrows(LedgerStore.StorageException.class, () -> new LedgerStore().list(file, null, null));
    }

    private Expense expense(String date, String category) {
        return new Expense(LocalDate.parse(date), category, new BigDecimal("9.90"), "test entry");
    }
}
