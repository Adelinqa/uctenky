package cz.uctenka;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Název a verze aplikace. Hodnoty doplňuje Maven z pom.xml
 * do souboru {@code app.properties} při sestavení.
 */
public record AppInfo(String name, String version) {

    static final String FALLBACK_NAME = "Účtenka CLI";
    static final String FALLBACK_VERSION = "neznámá";

    public static AppInfo load() {
        Properties p = new Properties();
        try (InputStream in = AppInfo.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            // použijí se výchozí hodnoty
        }
        return new AppInfo(clean(p.getProperty("app.name"), FALLBACK_NAME),
                clean(p.getProperty("app.version"), FALLBACK_VERSION));
    }

    private static String clean(String value, String fallback) {
        // Nevyplněný placeholder (např. při spuštění bez Mavenu) nahradíme výchozí hodnotou.
        if (value == null || value.isBlank() || value.contains("${")) {
            return fallback;
        }
        return value.strip();
    }

    @Override
    public String toString() {
        return name + " v" + version;
    }
}
