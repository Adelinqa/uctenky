package cz.uctenka;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @param name      název zboží
 * @param unitPrice jednotková cena včetně DPH
 * @param quantity  počet kusů (alespoň 1)
 * @param vatRate   sazba DPH
 */
public record Item(String name, BigDecimal unitPrice, int quantity, VatRate vatRate) {

    public static final int MAX_QUANTITY = 9999;

    public Item {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(vatRate, "vatRate");
        name = name.strip();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Název zboží nesmí být prázdný.");
        }
        if (unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Cena musí být větší než 0.");
        }
        if (quantity < 1 || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Počet kusů musí být 1 až " + MAX_QUANTITY + ".");
        }
        unitPrice = Money.scale(unitPrice);
    }

    // cena × počet
    public BigDecimal total() {
        return Money.scale(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }
}
