package cz.uctenka;

import java.math.BigDecimal;
import java.util.Objects;

public record Payment(Method method, BigDecimal total, BigDecimal toPay, BigDecimal received) {

    public enum Method { HOTOVE, KARTA }
// validace jestli neni castka nizsi nez ta k uhrade lol
    public Payment {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(total, "total");
        Objects.requireNonNull(toPay, "toPay");
        Objects.requireNonNull(received, "received");
        if (received.compareTo(toPay) < 0) {
            throw new IllegalArgumentException("Přijatá částka " + Money.format(received)
                    + " Kč je nižší než částka k úhradě " + Money.format(toPay) + " Kč.");
        }
    }

    public static Payment card(BigDecimal total) {
        BigDecimal exact = Money.scale(total);
        return new Payment(Method.KARTA, exact, exact, exact);
    }

    public static Payment cash(BigDecimal total, BigDecimal received) {
        return new Payment(Method.HOTOVE, Money.scale(total), cashAmount(total), Money.scale(received));
    }

    public static BigDecimal cashAmount(BigDecimal total) {
        return Money.roundToWholeCrowns(total);
    }

    public BigDecimal rounding() {
        return Money.scale(toPay.subtract(total));
    }

    public BigDecimal change() {
        return Money.scale(received.subtract(toPay));
    }
}
