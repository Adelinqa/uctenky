package cz.uctenka;

import java.util.Objects;

public record StoreInfo(String name, String street, String city, String ico, String dic,
                        String registerId, String footer) {

    public StoreInfo {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(street, "street");
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(ico, "ico");
        Objects.requireNonNull(dic, "dic");
        Objects.requireNonNull(registerId, "registerId");
        Objects.requireNonNull(footer, "footer");
    }
}
