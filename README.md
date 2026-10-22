# Účtenka CLI

Jednoduchý nástroj pro tisk pokladní účtenky z příkazové řádky – takové, jakou dostanete v samoobsluze, supermarketu nebo trafice.
## Autoři

| Jméno | GitHub |
|-------|--------|
| Adéla Pavlíčková | [@Adelinqa](https://github.com/Adelinqa) |
| Anna Jungmannová| [@Aletheie](https://github.com/Aletheie) |
| Zuzka Ševčíková | [@zuzisev](https://github.com/zuzisev) |
| Samuel Hlavatý | [@SamkoHla](https://github.com/SamkoHla) |

## Co program umí

- Údaje o obchodě (název, adresa, IČO, DIČ, číslo pokladny, patička) načítá ze souboru `obchod.properties`.
- Položky nákupu (název, cena za kus, počet kusů, sazba DPH 21 / 12 / 0 %) načítá interaktivně z příkazové řádky. Chybné hodnoty ohlásí a zeptá se znovu.
- Platba kartou (přesná částka) nebo hotově (zaokrouhlení na celé koruny a výpočet vrácení).
- Vytiskne účtenku o šířce 40 znaků včetně rekapitulace DPH podle sazeb.

### Ukázka

```
========================================
            Potraviny U Kulíšků
               Vše 123
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
## Požadavky

- Java 17 nebo novější (`java -version`)
- Maven 3.8+ (jen pro sestavení, `mvn -version`)

## Sestavení a spuštění

```bash
mvn clean package
java -jar target/uctenka-cli-1.0.0.jar
```

`mvn package` zkompiluje kód, spustí všechny unit testy a vytvoří spustitelný JAR v adresáři `target/`. K běhu stačí samotná Java, vývojové prostředí není potřeba.

### Parametry

```
-c, --config <soubor>  soubor s údaji o obchodě (výchozí obchod.properties v aktuálním adresáři)
-v, --version          vypíše verzi programu
-h, --help             vypíše nápovědu
```
### Zadávání nákupu

Program se postupně ptá na:

1. **Název zboží** – prázdný řádek ukončí zadávání položek.
2. **Cenu za kus** v Kč včetně DPH – např. `24,90` nebo `24.90`.
3. **Počet kusů** – Enter = 1.
4. **Sazbu DPH** – `21`, `12`, `0` (nebo `A`, `B`, `C`), Enter = 21 %.

Nakonec způsob platby: `K` = kartou (výchozí), `H` = hotově. U hotovosti se zadá přijatá částka (Enter = přesně).

Vstup lze i přesměrovat ze souboru (každá odpověď na samostatném řádku, soubor v UTF-8):

```bash
java -jar target/uctenka-cli-1.0.0.jar < nakup.txt
```

### Návratové kódy

| Kód | Význam |
|-----|--------|
| 0 | účtenka vytištěna |
| 2 | chybné parametry příkazové řádky |
| 3 | chyba konfiguračního souboru |
| 4 | vstup skončil před dokončením nákupu |

### Česká diakritika ve Windows

Pokud se místo písmen s diakritikou zobrazují otazníky, přepněte konzoli na UTF-8:

```bat
chcp 65001
java -Dstdout.encoding=UTF-8 -jar target\uctenka-cli-1.0.0.jar
```
## Konfigurace obchodu

Soubor `obchod.properties` (kódování UTF-8):

```properties
obchod.nazev=Potraviny U Lípy
obchod.ulice=Hlavní 123
obchod.mesto=110 00 Praha 1
obchod.ico=12345678
obchod.dic=CZ12345678
pokladna.cislo=1
uctenka.paticka=Děkujeme za nákup!
```

Povinné jsou `obchod.nazev`, `obchod.ulice`, `obchod.mesto`, `obchod.ico` (8 číslic) a `obchod.dic` (např. `CZ12345678`). Pokud něco chybí nebo je chybně, program vypíše srozumitelnou chybu a skončí s kódem 3.

## Testy

```bash
mvn test
```

Testy (JUnit 5) jsou v `src/test/java` a pokrývají:

| Třída | Co se testuje |
|-------|---------------|
| `MoneyTest` | načítání a formátování částek, zaokrouhlení na koruny |
| `VatRateTest` | sazby DPH a výpočet daně z ceny s DPH |
| `ItemTest` | položka, výpočet ceny, validace |
| `ReceiptTest` | součet nákupu a rekapitulace DPH |
| `PaymentTest` | platba kartou / hotově, zaokrouhlení, vrácení |
| `StoreInfoLoaderTest` | načítání konfigurace, chybějící a chybné údaje |
| `ReceiptFormatterTest` | vzhled účtenky, žádný řádek není širší než účtenka |
| `ConsoleInputTest` | interaktivní vstup včetně opakování po chybě |
| `MainTest` | celý běh programu od parametrů po vytištěnou účtenku |

## Struktura projektu

```
uctenka-cli/
├── pom.xml                       # Maven – verze, závislosti, spustitelný JAR
├── obchod.properties             # údaje o obchodě
├── README.md
└── src/
    ├── main/java/cz/uctenka/
    │   ├── Main.java             # vstupní bod, parametry
    │   ├── AppInfo.java          # název a verze (doplní Maven z pom.xml)
    │   ├── StoreInfo.java        # údaje o obchodě
    │   ├── StoreInfoLoader.java  # načtení údajů ze souboru
    │   ├── ConsoleInput.java     # interaktivní zadání nákupu
    │   ├── Item.java             # položka
    │   ├── Receipt.java          # nákup, součty, rekapitulace DPH
    │   ├── Payment.java          # platba, zaokrouhlení, vrácení
    │   ├── VatRate.java          # sazby DPH
    │   ├── Money.java            # práce s částkami
    │   └── ReceiptFormatter.java # textová podoba účtenky
    ├── main/resources/app.properties
    └── test/java/cz/uctenka/     # unit testy
```

## Jak vytvořit novou verzi

Verze je uvedena **jen na jednom místě – v `pom.xml`**. Do programu (`--version` a patička účtenky) se dostane automaticky přes `app.properties` při sestavení.

Číslo verze `MAJOR.MINOR.PATCH` zvyšujeme takto:

- **PATCH** (1.0.0 → 1.0.1) – oprava chyby, chování se jinak nemění,
- **MINOR** (1.0.1 → 1.1.0) – nová funkce, stávající konfigurace a vstup fungují dál,
- **MAJOR** (1.1.0 → 2.0.0) – nekompatibilní změna (např. jiný formát konfiguračního souboru).

Postup vydání:

```bash
# 1. aktuální stav
git checkout main
git pull

# 2. nastavit novou verzi v pom.xml (nebo ručně přepsat <version>)
mvn versions:set -DnewVersion=1.1.0 -DgenerateBackupPoms=false

# 3. upravit „Aktuální verze“ v tomto README

# 4. sestavit a otestovat – musí projít všechny testy
mvn clean package
java -jar target/uctenka-cli-1.1.0.jar --version

# 5. commit, tag a odeslání na GitHub
git commit -am "Verze 1.1.0"
git tag -a v1.1.0 -m "Verze 1.1.0"
git push origin main --tags
```

6. Na GitHubu v sekci **Releases → Draft a new release** vybrat tag `v1.1.0` a přiložit soubor `target/uctenka-cli-1.1.0.jar`.

### Sestavení bez Mavenu (ruční postup)

```bash
mkdir -p build
javac --release 17 -encoding UTF-8 -d build $(find src/main/java -name "*.java")
printf 'app.name=Účtenka CLI\napp.version=1.1.0\n' > build/app.properties
printf 'Main-Class: cz.uctenka.Main\n' > manifest.txt
jar cfm uctenka-cli-1.1.0.jar manifest.txt -C build .
```
