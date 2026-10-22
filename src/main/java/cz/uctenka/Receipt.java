package cz.uctenka;

/** Nákup, součty, rekapitulace DPH. */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Nákup – seznam položek a výpočty součtů a rekapitulace DPH. */
public final class Receipt {

    /** Rekapitulace jedné sazby DPH. */
    public record VatSummary(BigDecimal base, BigDecimal vat, BigDecimal gross) {
    }

    private final List<Item> items = new ArrayList<>();

    public void add(Item item) {
        items.add(Objects.requireNonNull(item, "item"));
    }

    public List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Celková cena nákupu včetně DPH. */
    public BigDecimal total() {
        BigDecimal sum = BigDecimal.ZERO;
        for (Item item : items) {
            sum = sum.add(item.total());
        }
        return Money.scale(sum);
    }

    /**
     * Rekapitulace DPH podle sazeb (seřazeno A, B, C). Obsahuje jen sazby,
     * které se v nákupu vyskytují. Základ + DPH vždy přesně dává celkovou cenu.
     */
    public Map<VatRate, VatSummary> vatBreakdown() {
        Map<VatRate, BigDecimal> grossByRate = new EnumMap<>(VatRate.class);
        for (Item item : items) {
            grossByRate.merge(item.vatRate(), item.total(), BigDecimal::add);
        }
        Map<VatRate, VatSummary> result = new EnumMap<>(VatRate.class);
        grossByRate.forEach((rate, gross) -> {
            BigDecimal vat = rate.vatFromGross(gross);
            result.put(rate, new VatSummary(Money.scale(gross.subtract(vat)), vat, Money.scale(gross)));
        });
        return result;
    }
}

