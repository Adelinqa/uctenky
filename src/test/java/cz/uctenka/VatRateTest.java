package cz.uctenka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VatRateTest {

    @Test
    void parsesPercentAndCode() {
        assertEquals(VatRate.ZAKLADNI, VatRate.parse("21"));
        assertEquals(VatRate.ZAKLADNI, VatRate.parse("21 %"));
        assertEquals(VatRate.SNIZENA, VatRate.parse("b"));
        assertEquals(VatRate.NULOVA, VatRate.parse("0"));
        assertEquals(VatRate.NULOVA, VatRate.parse("C"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"15", "10", "X", "", "  "})
    void rejectsUnknownRate(String text) {
        assertThrows(IllegalArgumentException.class, () -> VatRate.parse(text));
    }

    @Test
    void computesVatFromGrossPrice() {
        assertEquals(new BigDecimal("21.00"), VatRate.ZAKLADNI.vatFromGross(new BigDecimal("121.00")));
        assertEquals(new BigDecimal("12.00"), VatRate.SNIZENA.vatFromGross(new BigDecimal("112.00")));
        assertEquals(new BigDecimal("1.51"), VatRate.ZAKLADNI.vatFromGross(new BigDecimal("8.70")));
        assertEquals(new BigDecimal("0.00"), VatRate.NULOVA.vatFromGross(new BigDecimal("50.00")));
    }
}
