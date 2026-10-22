# Účtenka CLI (Receipt CLI)

A simple command-line tool for printing a cash register receipt, like the one you get at a self-service store, supermarket or newsagent's.

## Authors

| Name | GitHub |
|------|--------|
| Adéla Pavlíčková | [@Adelinqa](https://github.com/Adelinqa) |
| Anna Jungmannová | [@Aletheie](https://github.com/Aletheie) |
| Zuzka Ševčíková | [@zuzisev](https://github.com/zuzisev) |
| Samuel Hlavatý | [@SamkoHla](https://github.com/SamkoHla) |

## What the program does

- Loads store details (name, address, company ID (IČO), VAT ID (DIČ), register number, footer) from the `obchod.properties` file.
- Loads purchase items (name, price per unit, quantity, VAT rate 21 / 12 / 0 %) interactively from the command line. Invalid values are reported and the program asks again.
- Payment by card (exact amount) or in cash (rounded to whole crowns, with change calculation).
- Prints a 40-character-wide receipt including a VAT summary by rate.

### Example

```
========================================
            Supermarket U Kulíšků
               Institute 123
             110 00 Praha 67
     IČO: 12345678  DIČ: CZ12345678
========================================
Účtenka č.               20261022-160050
Datum                22.10.2026 16:00:50
Pokladna                               1
----------------------------------------
Rohlík
  3 ks x 2,90 Kč                  8,70 B
Plakát 67
  1 ks x 24,90 Kč                24,90 B
Dračí vejce
  2 ks x 19,90 Kč                39,80 A
Avokádo zralé
  1 ks x 25,00 Kč                25,00 C
----------------------------------------
CELKEM                          98,40 Kč
Zaokrouhlení                    -0,40 Kč
K ÚHRADĚ                        98,00 Kč
Hotově                         200,00 Kč
Vráceno                        102,00 Kč
----------------------------------------
Sazba       Základ        DPH     Celkem
A 21 %       32,89       6,91      39,80
B 12 %       30,00       3,60      33,60
C 0 %        25,00       0,00      25,00
========================================
           Děkujeme za nákup!
           Účtenka CLI v1.0.0
```

## Requirements

- Java 17 or newer (`java -version`)
- Maven 3.8+ (only for building, `mvn -version`)

## Building and running

```bash
mvn clean package
java -jar target/uctenka-cli-1.0.0.jar
```

`mvn package` compiles the code, runs all unit tests and creates an executable JAR in the `target/` directory. Only Java itself is needed to run it; no development environment is required.

### Parameters

```
-c, --config <file>  file with store details (default: obchod.properties in the current directory)
-v, --version        prints the program version
-h, --help           prints help
```

### Entering a purchase

The program asks step by step for:

1. **Item name** – an empty line ends the entry of items.
2. **Price per unit** in CZK including VAT – e.g. `24,90` or `24.90`.
3. **Quantity** – Enter = 1.
4. **VAT rate** – `21`, `12`, `0` (or `A`, `B`, `C`), Enter = 21 %.

Finally, the payment method: `K` = card (default), `H` = cash. For cash, the amount received is entered (Enter = exact amount).

Input can also be redirected from a file (each answer on a separate line, file in UTF-8):

```bash
java -jar target/uctenka-cli-1.0.0.jar < nakup.txt
```

### Exit codes

| Code | Meaning |
|------|---------|
| 0 | receipt printed |
| 2 | invalid command-line parameters |
| 3 | configuration file error |
| 4 | input ended before the purchase was completed |

### Czech diacritics on Windows

If question marks are displayed instead of letters with diacritics, switch the console to UTF-8:

```bat
chcp 65001
java -Dstdout.encoding=UTF-8 -jar target\uctenka-cli-1.0.0.jar
```

## Store configuration

The `obchod.properties` file (UTF-8 encoding):

```properties
obchod.nazev=Potraviny U Lípy
obchod.ulice=Hlavní 123
obchod.mesto=110 00 Praha 1
obchod.ico=12345678
obchod.dic=CZ12345678
pokladna.cislo=1
uctenka.paticka=Děkujeme za nákup!
```

`obchod.nazev`, `obchod.ulice`, `obchod.mesto`, `obchod.ico` (8 digits) and `obchod.dic` (e.g. `CZ12345678`) are required. If anything is missing or invalid, the program prints a clear error message and exits with code 3.

## Tests

```bash
mvn test
```

The tests (JUnit 5) are located in `src/test/java` and cover:

| Class | What is tested |
|-------|----------------|
| `MoneyTest` | parsing and formatting of amounts, rounding to whole crowns |
| `VatRateTest` | VAT rates and calculation of tax from a VAT-inclusive price |
| `ItemTest` | item, price calculation, validation |
| `ReceiptTest` | purchase total and VAT summary |
| `PaymentTest` | card / cash payment, rounding, change |
| `StoreInfoLoaderTest` | loading the configuration, missing and invalid data |
| `ReceiptFormatterTest` | receipt appearance, no line wider than the receipt |
| `ConsoleInputTest` | interactive input including retry after an error |
| `MainTest` | the whole program run from parameters to the printed receipt |

## Project structure

```
uctenka-cli/
├── pom.xml                       # Maven – version, dependencies, executable JAR
├── obchod.properties             # store details
├── README.md
└── src/
    ├── main/java/cz/uctenka/
    │   ├── Main.java             # entry point, parameters
    │   ├── AppInfo.java          # name and version (filled in by Maven from pom.xml)
    │   ├── StoreInfo.java        # store details
    │   ├── StoreInfoLoader.java  # loading details from a file
    │   ├── ConsoleInput.java     # interactive purchase entry
    │   ├── Item.java             # item
    │   ├── Receipt.java          # purchase, totals, VAT summary
    │   ├── Payment.java          # payment, rounding, change
    │   ├── VatRate.java          # VAT rates
    │   ├── Money.java            # handling of amounts
    │   └── ReceiptFormatter.java # text form of the receipt
    ├── main/resources/app.properties
    └── test/java/cz/uctenka/     # unit tests
```

## How to create a new version

The version is stated **in only one place – in `pom.xml`**. It gets into the program (`--version` and the receipt footer) automatically via `app.properties` during the build.

The version number `MAJOR.MINOR.PATCH` is increased as follows:

- **PATCH** (1.0.0 → 1.0.1) – bug fix, behavior otherwise unchanged,
- **MINOR** (1.0.1 → 1.1.0) – new feature, existing configuration and input keep working,
- **MAJOR** (1.1.0 → 2.0.0) – incompatible change (e.g. a different configuration file format).

Release procedure:

```bash
# 1. current state
git checkout main
git pull

# 2. set the new version in pom.xml (or overwrite <version> manually)
mvn versions:set -DnewVersion=1.1.0 -DgenerateBackupPoms=false

# 3. update "Current version" in this README

# 4. build and test – all tests must pass
mvn clean package
java -jar target/uctenka-cli-1.1.0.jar --version

# 5. commit, tag and push to GitHub
git commit -am "Verze 1.1.0"
git tag -a v1.1.0 -m "Verze 1.1.0"
git push origin main --tags
```

6. On GitHub, go to **Releases → Draft a new release**, select the tag `v1.1.0` and attach the file `target/uctenka-cli-1.1.0.jar`.

### Building without Maven (manual procedure)

```bash
mkdir -p build
javac --release 17 -encoding UTF-8 -d build $(find src/main/java -name "*.java")
printf 'app.name=Účtenka CLI\napp.version=1.1.0\n' > build/app.properties
printf 'Main-Class: cz.uctenka.Main\n' > manifest.txt
jar cfm uctenka-cli-1.1.0.jar manifest.txt -C build .
```

---

Notes on what I preserved: the sample receipt, property keys and values, file and class names, commands, and commit/tag messages are unchanged, since they are the program's actual Czech output or literal code. I also tidied a few stray line breaks in the original Markdown (e.g. in the bullet lists) without changing any content.