package cz.uctenka;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Načte {@link StoreInfo} ze souboru ve formátu {@code .properties} (UTF-8).
 *
 * <pre>
 * obchod.nazev=Potraviny U Lípy
 * obchod.ulice=Hlavní 123
 * obchod.mesto=110 00 Praha 1
 * obchod.ico=12345678
 * obchod.dic=CZ12345678
 * pokladna.cislo=1                     (nepovinné)
 * uctenka.paticka=Děkujeme za nákup!   (nepovinné)
 * </pre>
 */
public final class StoreInfoLoader {

    public static final String DEFAULT_REGISTER = "1";
    public static final String DEFAULT_FOOTER = "Děkujeme za nákup!";

    /** Chyba v konfiguračním souboru. */
    public static class ConfigException extends Exception {
        private static final long serialVersionUID = 1L;

        public ConfigException(String message) {
            super(message);
        }

        public ConfigException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public StoreInfo load(Path file) throws ConfigException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return load(reader);
        } catch (NoSuchFileException e) {
            throw new ConfigException("Konfigurační soubor nebyl nalezen: " + file.toAbsolutePath(), e);
        } catch (IOException e) {
            throw new ConfigException("Konfigurační soubor nelze přečíst: " + file
                    + " (" + e.getMessage() + ")", e);
        }
    }

    public StoreInfo loadFromString(String content) throws ConfigException {
        try {
            return load(new StringReader(content));
        } catch (IOException e) {
            throw new ConfigException("Konfiguraci nelze přečíst: " + e.getMessage(), e);
        }
    }

    private StoreInfo load(Reader reader) throws IOException, ConfigException {
        Properties p = new Properties();
        try {
            p.load(reader);
        } catch (IllegalArgumentException e) {
            throw new ConfigException("Konfigurační soubor má chybný formát: " + e.getMessage(), e);
        }

        String name = required(p, "obchod.nazev");
        String street = required(p, "obchod.ulice");
        String city = required(p, "obchod.mesto");
        String ico = required(p, "obchod.ico").replace(" ", "");
        String dic = required(p, "obchod.dic").replace(" ", "").toUpperCase();
        String register = optional(p, "pokladna.cislo", DEFAULT_REGISTER);
        String footer = optional(p, "uctenka.paticka", DEFAULT_FOOTER);

        if (!ico.matches("\\d{8}")) {
            throw new ConfigException("IČO musí mít přesně 8 číslic, zadáno: „" + ico + "“.");
        }
        if (!dic.matches("[A-Z]{2}\\d{8,10}")) {
            throw new ConfigException("DIČ musí mít tvar např. CZ12345678, zadáno: „" + dic + "“.");
        }
        return new StoreInfo(name, street, city, ico, dic, register, footer);
    }

    private static String required(Properties p, String key) throws ConfigException {
        String value = p.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new ConfigException("V konfiguraci chybí povinný údaj „" + key + "“.");
        }
        return value.strip();
    }

    private static String optional(Properties p, String key, String defaultValue) {
        String value = p.getProperty(key);
        return (value == null || value.isBlank()) ? defaultValue : value.strip();
    }
}
