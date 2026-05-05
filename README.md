# GeoLang - Język dziedzinowy do generowania figur 3D przy użyciu JavaFX

GeoLang to prosty język dziedzinowy (DSL) umożliwiający deklarowanie i modyfikowanie podstawowych figur geometrycznych (punktów, linii, okręgów) oraz ich wizualizację w oknie JavaFX.
Projekt powstał jako praca z generatorami parserów (ANTLR 4) i pokazuje pełną integrację: gramatyka → interpreter → wizualizacja.

## Funkcjonalności

- Deklaracje zmiennych typów: `float`, `point`, `line`, `circle`.
- Wyrażenia arytmetyczne na zmiennych `float`.
- Dostęp i przypisanie do pól (np. `p.x = 10.0`, `c.r = 5.0`).
- Wywoływanie metod na obiektach (obecnie `render()` do rysowania oraz `move()` dla punktu).
- Jawne żądanie narysowania figury przez wywołanie `render()`.
- Wizualizacja w oknie JavaFX.

## Przykład kodu GeoLang

Poniższy skrypt definiuje kilka figur i rysuje tylko wybrane z nich:

```plaintext
// deklaracje liczb zmiennoprzecinkowych
float radius = 50.0;
float dx = 10.0;
float dy = 20.0;

// punkt
point p = (100.0, 200.0);
point p2 = (dx, dy);

// linia
line l = ((0.0, 0.0), (30.0, 40.0));

// okrąg
circle c = ((p.x, p.y), radius);

// zmiana współrzędnych
p.x = 150.0;
c.r = radius * 0.5;

// jawne renderowanie figur (tylko te zostaną narysowane)
l.render();
c.render();
// punkt p i p2 nie zostaną narysowane
```

## Struktura projektu
```plaintext
src/main/java/
├── interpreter/
│   ├── variables/
│   │   ├── VarType.java            # Interfejs wszystkich typów
│   │   ├── VarTypeEnum.java        # Enum typów (FLOAT, POINT, CIRCLE, LINE)
│   │   ├── FloatType.java          # Typ liczbowy
│   │   ├── PointType.java          # Punkt (x,y)
│   │   ├── LineType.java           # Linia (p1, p2)
│   │   ├── CircleType.java         # Okrąg (środek, promień)
│   ├── drawable/
│   │   ├── Drawable.java           # Interfejs rysowalnych obiektów
│   │   ├── DrawablePoint.java      # Rysowanie punktu
│   │   ├── DrawableLine.java       # Rysowanie linii
│   │   ├── DrawableCircle.java     # Rysowanie okręgu
│   │   └── DrawCollector.java      # Kolektor figur do wyrenderowania
│   ├── Kolorowy.java               # Visitor – interpreter języka
│   ├── DrawingApp.java             # Aplikacja JavaFX (okno i rysowanie)
│   └── Start.java                  # Punkt wejścia: parsowanie, interpretacja, uruchomienie GUI
├── grammar/                        # (generowane przez ANTLR)
│   ├── GeoLangLexer.java
│   ├── GeoLangParser.java
│   └── ... (listenery, wizytatory)
├── SymbolTable/
│   └── LocalSymbols.java           # Tablica symboli z obsługą zakresów
└── resources/                      # Opcjonalne zasoby (np. ikona)

src/main/antlr4/
├── GeoLangLexer.g4                  # Gramatyka leksera
└── GeoLangParser.g4                 # Gramatyka parsera
```

## Działanie krok po kroku

1. Plik źródłowy – program wczytuje treść skryptu GeoLang (domyślnie we.GeoLang).

2. Parsing – ANTLR tworzy drzewo składniowe (CST) na podstawie gramatyki.

3. Interpretacja – Visitor Kolorowy odwiedza węzły drzewa:

   - wykonuje deklaracje zmiennych,

   - oblicza wyrażenia,

   - wywołuje metody (np. render()).

4. Kolekcja rysunków – Gdy interpreter napotka render(), dodaje odpowiedni obiekt Drawable do DrawCollector.

5. Wizualizacja – Po zakończeniu interpretacji, lista figur z DrawCollector przekazywana jest do DrawingApp, która otwiera okno JavaFX i rysuje figury na płótnie.