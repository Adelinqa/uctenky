package cz.uctenka;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;

public final class Main {

    public static final String DEFAULT_CONFIG = "obchod.properties";

    static final int OK = 0;
    static final int ERR_USAGE = 2;
    static final int ERR_CONFIG = 3;
    static final int ERR_INPUT = 4;

    private Main() {
    }

    public static void main(String[] args) {
        Console console = System.console();
        Charset inputCharset = console != null ? console.charset() : Charset.defaultCharset();
        int code = run(args, System.in, inputCharset, System.out, System.err, Clock.systemDefaultZone());
        System.exit(code);
    }

    static int run(String[] args, InputStream stdin, Charset charset,
                   PrintStream out, PrintStream err, Clock clock) {
        AppInfo app = AppInfo.load();
        String configPath = DEFAULT_CONFIG;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h", "--help" -> {
                    printHelp(out, app);
                    return OK;
                }
                case "-v", "--version" -> {
                    out.println(app);
                    return OK;
                }
                case "-c", "--config" -> {
                    if (i + 1 >= args.length) {
                        err.println("Chyba: za " + args[i] + " chybí cesta ke konfiguračnímu souboru.");
                        return ERR_USAGE;
                    }
                    configPath = args[++i];
                }
                default -> {
                    err.println("Chyba: neznámý parametr „" + args[i] + "“. Nápověda: --help");
                    return ERR_USAGE;
                }
            }
        }

        StoreInfo store;
        try {
            store = new StoreInfoLoader().load(Path.of(configPath));
        } catch (StoreInfoLoader.ConfigException e) {
            err.println("Chyba konfigurace: " + e.getMessage());
            return ERR_CONFIG;
        } catch (java.nio.file.InvalidPathException e) {
            err.println("Chyba konfigurace: neplatná cesta „" + configPath + "“.");
            return ERR_CONFIG;
        }

        out.println(app + " – " + store.name());
        out.println();

        ConsoleInput input = new ConsoleInput(
                new BufferedReader(new InputStreamReader(stdin, charset)), out);
        try {
            Receipt receipt = input.readReceipt();
            Payment payment = input.readPayment(receipt.total());
            LocalDateTime now = LocalDateTime.now(clock).withNano(0);
            out.println();
            out.print(new ReceiptFormatter().format(store, receipt, payment, now, app));
            out.flush();
            return OK;
        } catch (ConsoleInput.InputEndedException e) {
            out.println();
            err.println("Chyba: " + e.getMessage() + " Účtenka nebyla vytištěna.");
            return ERR_INPUT;
        }
    }

    private static void printHelp(PrintStream out, AppInfo app) {
        out.println(app);
        out.println("Tisk jednoduché pokladní účtenky.");
        out.println();
        out.println("Použití: java -jar uctenka-cli.jar [volby]");
        out.println();
        out.println("Volby:");
        out.println("  -c, --config <soubor>  soubor s údaji o obchodě (výchozí " + DEFAULT_CONFIG + ")");
        out.println("  -v, --version          vypíše verzi programu");
        out.println("  -h, --help             vypíše tuto nápovědu");
        out.println();
        out.println("Položky nákupu se zadávají interaktivně. Prázdný název ukončí zadávání.");
    }
}
