package cz.uctenka;

/** Interaktivní zadání nákupu. */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;

/**
 * Interaktivně načte nákup od uživatele z příkazové řádky.
 * Při chybném vstupu vypíše chybu a zeptá se znovu.
 */
public final class ConsoleInput {

    /** Vstup skončil dřív, než byl nákup kompletní (např. Ctrl+D / konec souboru). */
    public static class InputEndedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public InputEndedException() {
            super("Vstup byl ukončen dříve, než byl nákup dokončen.");
        }
    }

    private final BufferedReader in;
    private final PrintStream out;

    public ConsoleInput(BufferedReader in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    /** Načte položky, dokud uživatel nezadá prázdný název. */
    public Receipt readReceipt() {
        Receipt receipt = new Receipt();
        out.println("Zadávejte položky nákupu. Prázdný název ukončí zadávání.");
        while (true) {
            out.println();
            out.print("Název zboží: ");
            String name = readLine();
            if (name == null) {
                if (receipt.isEmpty()) {
                    throw new InputEndedException();
                }
                return receipt;
            }
            if (name.isBlank()) {
                if (receipt.isEmpty()) {
                    out.println("Chyba: účtenka musí obsahovat alespoň jednu položku.");
                    continue;
                }
                return receipt;
            }
            BigDecimal price = readPrice();
            int quantity = readQuantity();
            VatRate vat = readVatRate();
            Item item = new Item(name, price, quantity, vat);
            receipt.add(item);
            out.println("Přidáno: " + item.name() + " – " + Money.format(item.total()) + " Kč"
                    + " (mezisoučet " + Money.format(receipt.total()) + " Kč)");
        }
    }

    /** Zeptá se na způsob platby (a u hotovosti na přijatou částku). */
    public Payment readPayment(BigDecimal total) {
        out.println();
        out.println("Celkem k úhradě: " + Money.format(total) + " Kč");
        while (true) {
            out.print("Platba [H]otově / [K]artou (výchozí K): ");
            String answer = requireLine().strip().toUpperCase();
            if (answer.isEmpty() || answer.equals("K")) {
                return Payment.card(total);
            }
            if (answer.equals("H")) {
                return readCash(total);
            }
            out.println("Chyba: zadejte H nebo K.");
        }
    }

    private Payment readCash(BigDecimal total) {
        BigDecimal toPay = Payment.cashAmount(total);
        while (true) {
            out.print("Přijato hotově (K úhradě " + Money.format(toPay) + " Kč, Enter = přesně): ");
            String text = requireLine().strip();
            try {
                BigDecimal received = text.isEmpty() ? toPay : Money.parse(text);
                return Payment.cash(total, received);
            } catch (IllegalArgumentException e) {
                out.println("Chyba: " + e.getMessage());
            }
        }
    }

    private BigDecimal readPrice() {
        while (true) {
            out.print("Cena za kus v Kč: ");
            try {
                BigDecimal price = Money.parse(requireLine());
                if (price.signum() > 0) {
                    return price;
                }
                out.println("Chyba: cena musí být větší než 0.");
            } catch (IllegalArgumentException e) {
                out.println("Chyba: " + e.getMessage());
            }
        }
    }

    private int readQuantity() {
        while (true) {
            out.print("Počet kusů (výchozí 1): ");
            String text = requireLine().strip();
            if (text.isEmpty()) {
                return 1;
            }
            try {
                int q = Integer.parseInt(text);
                if (q >= 1 && q <= Item.MAX_QUANTITY) {
                    return q;
                }
            } catch (NumberFormatException e) {
                // spadne na chybovou hlášku níže
            }
            out.println("Chyba: počet kusů musí být celé číslo 1 až " + Item.MAX_QUANTITY + ".");
        }
    }

    private VatRate readVatRate() {
        while (true) {
            out.print("Sazba DPH 21 / 12 / 0 % (výchozí 21): ");
            String text = requireLine().strip();
            if (text.isEmpty()) {
                return VatRate.ZAKLADNI;
            }
            try {
                return VatRate.parse(text);
            } catch (IllegalArgumentException e) {
                out.println("Chyba: " + e.getMessage());
            }
        }
    }

    private String requireLine() {
        String line = readLine();
        if (line == null) {
            throw new InputEndedException();
        }
        return line;
    }

    private String readLine() {
        try {
            String line = in.readLine();
            // Odstranění BOM, který může přidat Windows při přesměrování souboru.
            if (line != null && !line.isEmpty() && line.charAt(0) == '﻿') {
                line = line.substring(1);
            }
            return line;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

