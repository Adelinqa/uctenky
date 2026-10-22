package cz.uctenka;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public final class Money {

    private static final Pattern AMOUNT = Pattern.compile("\\d{1,9}([.,]\\d{1,2})?");

    private Money() {
    }

    public static BigDecimal parse(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Částka nesmí být prázdná.");
        }
        String trimmed = text.trim().replace(" ", "");
        if (!AMOUNT.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    "Neplatná částka „" + text.trim() + "“. Zadejte např. 24,90.");
        }
        //nahrazuje nám případné čárky na tečky hihi
        return scale(new BigDecimal(trimmed.replace(',', '.')));
    }
    //hezky nám tám dá maximálně dvě desetinná místa
    public static BigDecimal scale(BigDecimal amount) {

        return amount.setScale(2, RoundingMode.HALF_UP);
    }
    // zaokrouhluje na cely korunky
    public static BigDecimal roundToWholeCrowns(BigDecimal amount) {
        return amount.setScale(0, RoundingMode.HALF_UP).setScale(2, RoundingMode.UNNECESSARY);
    }
    public static String format(BigDecimal amount) {
        return scale(amount).toPlainString().replace('.', ',');
    }
}
