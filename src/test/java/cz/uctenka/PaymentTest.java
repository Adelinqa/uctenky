package cz.uctenka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void cardPaysExactAmount() {
        Payment p = Payment.card(new BigDecimal("33.60"));
        assertEquals(Payment.Method.KARTA, p.method());
        assertEquals(new BigDecimal("33.60"), p.toPay());
        assertEquals(new BigDecimal("0.00"), p.rounding());
        assertEquals(new BigDecimal("0.00"), p.change());
    }

    @Test
    void cashRoundsUpAndReturnsChange() {
        Payment p = Payment.cash(new BigDecimal("33.60"), new BigDecimal("100"));
        assertEquals(new BigDecimal("34.00"), p.toPay());
        assertEquals(new BigDecimal("0.40"), p.rounding());
        assertEquals(new BigDecimal("66.00"), p.change());
    }

    @Test
    void cashRoundsDown() {
        Payment p = Payment.cash(new BigDecimal("33.40"), new BigDecimal("33"));
        assertEquals(new BigDecimal("33.00"), p.toPay());
        assertEquals(new BigDecimal("-0.40"), p.rounding());
        assertEquals(new BigDecimal("0.00"), p.change());
    }

    @Test
    void rejectsInsufficientCash() {
        assertThrows(IllegalArgumentException.class,
                () -> Payment.cash(new BigDecimal("33.60"), new BigDecimal("33.99")));
    }
}
