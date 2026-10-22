package cz.uctenka;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/** Sestaví textovou podobu účtenky pevné šířky (jako z pokladní tiskárny). */
public final class ReceiptFormatter {

    public static final int DEFAULT_WIDTH = 40;
    static final int MIN_WIDTH = 32;

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final DateTimeFormatter NUMBER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final int width;

    public ReceiptFormatter() {
        this(DEFAULT_WIDTH);
    }

    public ReceiptFormatter(int width) {
        if (width < MIN_WIDTH) {
            throw new IllegalArgumentException("Šířka účtenky musí být alespoň " + MIN_WIDTH + " znaků.");
        }
        this.width = width;
    }

    /** Číslo účtenky odvozené od času vystavení, např. 20261022-143005. */
    public static String receiptNumber(LocalDateTime time) {
        return NUMBER.format(time);
    }

    public String format(StoreInfo store, Receipt receipt, Payment payment,
                         LocalDateTime time, AppInfo app) {
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(receipt, "receipt");
        Objects.requireNonNull(payment, "payment");
        Objects.requireNonNull(time, "time");
        Objects.requireNonNull(app, "app");
        if (receipt.isEmpty()) {
            throw new IllegalArgumentException("Účtenka neobsahuje žádnou položku.");
        }
        if (payment.total().compareTo(receipt.total()) != 0) {
            throw new IllegalArgumentException("Platba neodpovídá ceně nákupu.");
        }

        StringBuilder sb = new StringBuilder();
        String doubleLine = "=".repeat(width);
        String line = "-".repeat(width);

        // Hlavička
        sb.append(doubleLine).append('\n');
        sb.append(center(store.name())).append('\n');
        sb.append(center(store.street())).append('\n');
        sb.append(center(store.city())).append('\n');
        sb.append(center("IČO: " + store.ico() + "  DIČ: " + store.dic())).append('\n');
        sb.append(doubleLine).append('\n');
        sb.append(leftRight("Účtenka č.", receiptNumber(time))).append('\n');
        sb.append(leftRight("Datum", DATE_TIME.format(time))).append('\n');
        sb.append(leftRight("Pokladna", store.registerId())).append('\n');
        sb.append(line).append('\n');

        // Položky
        for (Item item : receipt.items()) {
            sb.append(truncate(item.name(), width)).append('\n');
            String detail = "  " + item.quantity() + " ks x " + Money.format(item.unitPrice()) + " Kč";
            sb.append(leftRight(detail, Money.format(item.total()) + " " + item.vatRate().code()))
                    .append('\n');
        }
        sb.append(line).append('\n');

        // Součty a platba
        sb.append(leftRight("CELKEM", money(payment.total()))).append('\n');
        if (payment.rounding().signum() != 0) {
            sb.append(leftRight("Zaokrouhlení", money(payment.rounding()))).append('\n');
        }
        sb.append(leftRight("K ÚHRADĚ", money(payment.toPay()))).append('\n');
        if (payment.method() == Payment.Method.KARTA) {
            sb.append(leftRight("Platba kartou", money(payment.received()))).append('\n');
        } else {
            sb.append(leftRight("Hotově", money(payment.received()))).append('\n');
            sb.append(leftRight("Vráceno", money(payment.change()))).append('\n');
        }
        sb.append(line).append('\n');

        // Rekapitulace DPH
        sb.append(vatRow("Sazba", "Základ", "DPH", "Celkem")).append('\n');
        for (Map.Entry<VatRate, Receipt.VatSummary> e : receipt.vatBreakdown().entrySet()) {
            VatRate rate = e.getKey();
            Receipt.VatSummary s = e.getValue();
            String label = rate.code() + " " + rate.percent() + " %";
            String base = Money.format(s.base());
            String vat = Money.format(s.vat());
            String gross = Money.format(s.gross());
            if (fitsVatColumn(base) && fitsVatColumn(vat) && fitsVatColumn(gross)) {
                sb.append(vatRow(label, base, vat, gross)).append('\n');
            } else {
                // Příliš velké částky se do sloupců nevejdou – vypíšou se pod sebe.
                sb.append(label).append('\n');
                sb.append(leftRight("  Základ", base)).append('\n');
                sb.append(leftRight("  DPH", vat)).append('\n');
                sb.append(leftRight("  Celkem", gross)).append('\n');
            }
        }

        // Patička
        sb.append(doubleLine).append('\n');
        sb.append(center(store.footer())).append('\n');
        sb.append(center(app.toString())).append('\n');
        return sb.toString();
    }

    private String money(java.math.BigDecimal amount) {
        return Money.format(amount) + " Kč";
    }

    private int vatColumnWidth() {
        return (width - 7) / 3;
    }

    /** Hodnota se vejde do sloupce i s mezerou oddělující ji od sousedního sloupce. */
    private boolean fitsVatColumn(String value) {
        return value.length() < vatColumnWidth();
    }

    private String vatRow(String rate, String base, String vat, String gross) {
        int col = vatColumnWidth();
        int first = width - 3 * col;
        return pad(truncate(rate, first), first)
                + leftPad(base, col) + leftPad(vat, col) + leftPad(gross, col);
    }

    String center(String text) {
        String t = truncate(text, width);
        int left = (width - t.length()) / 2;
        return " ".repeat(left) + t;
    }

    String leftRight(String left, String right) {
        String r = truncate(right, width);
        int space = width - r.length() - 1;
        String l = space > 0 ? truncate(left, space) : "";
        return l + " ".repeat(width - l.length() - r.length()) + r;
    }

    static String truncate(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return max <= 1 ? text.substring(0, max) : text.substring(0, max - 1) + "…";
    }

    private static String pad(String text, int len) {
        return text + " ".repeat(Math.max(0, len - text.length()));
    }

    private static String leftPad(String text, int len) {
        return " ".repeat(Math.max(0, len - text.length())) + text;
    }
}
