package cz.uctenka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void totalIsPriceTimesQuantity() {
        Item item = new Item("Rohlík", new BigDecimal("2.90"), 3, VatRate.SNIZENA);
        assertEquals(new BigDecimal("8.70"), item.total());
    }

    @Test
    void trimsName() {
        assertEquals("Mléko", new Item("  Mléko ", BigDecimal.TEN, 1, VatRate.SNIZENA).name());
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new Item("  ", BigDecimal.ONE, 1, VatRate.ZAKLADNI));
        assertThrows(IllegalArgumentException.class,
                () -> new Item("X", BigDecimal.ZERO, 1, VatRate.ZAKLADNI));
        assertThrows(IllegalArgumentException.class,
                () -> new Item("X", new BigDecimal("-1"), 1, VatRate.ZAKLADNI));
        assertThrows(IllegalArgumentException.class,
                () -> new Item("X", BigDecimal.ONE, 0, VatRate.ZAKLADNI));
        assertThrows(IllegalArgumentException.class,
                () -> new Item("X", BigDecimal.ONE, Item.MAX_QUANTITY + 1, VatRate.ZAKLADNI));
        assertThrows(NullPointerException.class,
                () -> new Item("X", BigDecimal.ONE, 1, null));
    }
}
