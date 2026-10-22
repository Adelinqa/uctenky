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