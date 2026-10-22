package cz.uctenka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {

    @Test
    void parsesCommaAndDot() {
        assertEquals(new BigDecimal("24.90"), Money.parse("24,90"));
        assertEquals(new BigDecimal("24.90"), Money.parse("24.9"));
        assertEquals(new BigDecimal("15.00"), Money.parse(" 15 "));
        assertEquals(new BigDecimal("1250.00"), Money.parse("1 250"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "-5", "1,234", "1e5", "12,", ",5", "1.2.3"})
    void rejectsInvalidAmounts(String text) {
        assertThrows(IllegalArgumentException.class, () -> Money.parse(text));
    }

    @Test
    void rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> Money.parse(null));
    }

    @Test
    void formatsWithCommaAndTwoDecimals() {
        assertEquals("8,70", Money.format(new BigDecimal("8.7")));
        assertEquals("0,00", Money.format(BigDecimal.ZERO));
        assertEquals("-0,40", Money.format(new BigDecimal("-0.4")));
        assertEquals("1234,57", Money.format(new BigDecimal("1234.565")));
    }

    @Test
    void roundsToWholeCrowns() {
        assertEquals(new BigDecimal("34.00"), Money.roundToWholeCrowns(new BigDecimal("33.60")));
        assertEquals(new BigDecimal("33.00"), Money.roundToWholeCrowns(new BigDecimal("33.49")));
        assertEquals(new BigDecimal("34.00"), Money.roundToWholeCrowns(new BigDecimal("33.50")));
    }
}
