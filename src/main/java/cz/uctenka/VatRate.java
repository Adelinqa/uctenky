package cz.uctenka;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum VatRate {
    ZAKLADNI(21, 'A'),
    SNIZENA(12, 'B'),
    NULOVA(0, 'C');

    private final int percent;
    private final char code;

    VatRate(int percent, char code) {
        this.percent = percent;
        this.code = code;
    }

    public int percent() {
        return percent;
    }

    public char code() {
        return code;
    }
    
    public static VatRate parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Sazba DPH nesmí být prázdná.");
        }
        String t = text.trim().replace("%", "").trim().toUpperCase();
        for (VatRate rate : values()) {
            if (t.equals(String.valueOf(rate.code)) || t.equals(String.valueOf(rate.percent))) {
                return rate;
            }
        }
        throw new IllegalArgumentException(
                "Neznámá sazba DPH „" + text.trim() + "“. Povolené: 21, 12, 0 (nebo A, B, C).");
    }

    /**
     * Spočítá DPH obsažené v ceně s daní (koeficientový výpočet shora).
     * Např. z 121 Kč při 21 % je DPH 21 Kč.
     */
    public BigDecimal vatFromGross(BigDecimal gross) {
        if (percent == 0) {
            return Money.scale(BigDecimal.ZERO);
        }
        return gross.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100L + percent), 2, RoundingMode.HALF_UP);
    }
}
