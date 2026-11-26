package edu.nuistech.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LedgerCliTest {
    record Result(int code, String out, String err) {}

    @TempDir
    Path temp;

    @Test
    void helpAndEmptyQueryHaveSuccessfulExitStatuses() {
        assertTrue(run("--help").out().contains("summary"));
        assertEquals(0, run("list").code());
        assertTrue(run("summary", "--month", "2026-11").out().contains("2026-11 total: CNY 0.00"));
        assertTrue(run("summary").out().contains("No expenses to summarize."));
    }

    @Test
    void requiresTheAddOptionsAndRejectsUnknownCommands() {
        assertEquals(2, run("add", "--amount", "20").code());
        assertEquals(2, run("subtraction").code());
        assertEquals(2, run("summary", "--month", "2026-13").code());
        assertEquals(2, run("list", "--category", "").code());
    }

    @Test
    void rejectsInvalidInputBeforeCreatingTheLedger() {
        var badDate = run("add", "--date", "2026-02-29", "--category", "food", "--amount", "8.00");
        var badAmount = run("add", "--date", "2026-11-26", "--category", "food", "--amount", "8.001");
        assertEquals(2, badDate.code());
        assertEquals(2, badAmount.code());
        assertTrue(badAmount.err().contains("at most two decimal places"));
        assertFalse(Files.exists(temp.resolve("data")));
    }

    @Test
    void canReadWhatAPreviousInvocationRecorded() {
        assertEquals(0, run("add", "--date", "2026-11-26", "--category", "Food", "--amount", "8.80",
                "--note", "Lunch break").code());
        assertEquals(0, run("add", "--date", "2026-11-27", "--category", "Transport", "--amount", "2.20").code());
        var match = run("list", "--category", "FOOD", "--month", "2026-11");
        assertEquals(0, match.code());
        assertTrue(match.out().contains("food\t8.80\tLunch break"));
        assertFalse(match.out().contains("transport"));
        var summary = run("summary", "--month", "2026-11");
        assertEquals(0, summary.code());
        assertTrue(summary.out().contains("2026-11 total: CNY 11.00"));
        assertTrue(summary.out().contains("  transport: CNY 2.20"));
    }

    @Test
    void reportsStorageErrorsOnStandardError() throws java.io.IOException {
        Path blocked = Files.createFile(temp.resolve("blocked"));
        var result = invoke("list", "--data-dir", blocked.toString());
        assertEquals(1, result.code());
        assertTrue(result.err().contains("Could not read or write"));
        assertTrue(result.out().isEmpty());
    }

    private Result run(String... args) {
        var withDirectory = new String[args.length + 2];
        withDirectory[0] = "--data-dir";
        withDirectory[1] = temp.resolve("data").toString();
        System.arraycopy(args, 0, withDirectory, 2, args.length);
        return invoke(withDirectory);
    }

    private Result invoke(String... args) {
        var out = new StringWriter();
        var err = new StringWriter();
        int code = new LedgerCli(new LedgerStore()).run(args, new PrintWriter(out), new PrintWriter(err));
        return new Result(code, out.toString(), err.toString());
    }
}
