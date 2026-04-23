package interpreter;

import grammar.GeoLangLexer;
import grammar.GeoLangParser;
import interpreter.drawable.Drawable;
import interpreter.drawable.DrawableCircle;
import interpreter.drawable.DrawableLine;
import interpreter.drawable.DrawablePoint;
import interpreter.variables.CircleType;
import interpreter.variables.LineType;
import interpreter.variables.PointType;
import interpreter.variables.VarType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DrawingApp extends Application {
    private static List<Drawable> figures = new ArrayList<>();
    private Canvas canvas;
    private TextArea codeInput;
    private static double minX = Double.POSITIVE_INFINITY;
    private static double maxX = Double.NEGATIVE_INFINITY;
    private static double minY = Double.POSITIVE_INFINITY;
    private static double maxY = Double.NEGATIVE_INFINITY;
    private boolean isDarkMode = false;

    public static void setFigures(List<Drawable> figs) {
        figures = figs;
        computeBounds(figs);
    }

    private static void computeBounds(List<Drawable> figs) {
        // Dla uproszczenia zakładamy, że każdy Drawable potrafi podać swoje granice
        // (można rozszerzyć interfejs Drawable o metodę bounds())
        // Tutaj pomijam – w zaawansowanej wersji można dodać.
    }

    private static List<Drawable> convertToDrawables(Map<String, VarType> variables) {
        List<Drawable> drawables = new ArrayList<>();
        for (VarType var : variables.values()) {
            switch (var.getType()) {
                case POINT -> {
                    PointType p = (PointType) var;
                    drawables.add(new DrawablePoint(p.x.value, p.y.value));
                }
                case LINE -> {
                    LineType l = (LineType) var;
                    drawables.add(new DrawableLine(
                            l.p1.x.value, l.p1.y.value,
                            l.p2.x.value, l.p2.y.value
                    ));
                }
                case CIRCLE -> {
                    CircleType c = (CircleType) var;
                    drawables.add(new DrawableCircle(
                            c.c.x.value, c.c.y.value,
                            c.r.value
                    ));
                }
                // FLOAT ignorujemy
            }
        }
        return drawables;
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- PASEK NARZĘDZI NA GÓRZE ---
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(0, 0, 10, 0)); // Odstęp pod paskiem

        Button clearButton = new Button("Wyczyść");
        Button exampleButton = new Button("Przykład");
        Button saveButton = new Button("Zapisz PNG");
        Button colorModeButton = new Button("Dark/Light");

        // Stylowanie przycisków (opcjonalnie)
        saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        clearButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        clearButton.setStyle("-fx-background-color: #68476d; -fx-text-fill: white;");

        toolbar.getChildren().addAll(exampleButton, clearButton, saveButton,  colorModeButton);
        root.setTop(toolbar); // Umieszczamy pasek na górze

        // --- AKCJE DLA PRZYCISKÓW ---

        // 1. Wyczyść
        clearButton.setOnAction(e -> {
            codeInput.clear();
//            errorConsole.clear();
            figures.clear();
            draw(canvas.getGraphicsContext2D());
        });

        // 2. Przykładowe dane
        exampleButton.setOnAction(e -> {
            codeInput.setText(
                    "point a = (100, 100);\n" +
                    "point b = (300, 300);\n" +
                    "point d = (500, 200);\n" +
                    "line l = (a, b);\n" +
                    "circle c1 = (d, 50);\n" +
                    "circle c2 = (d, 100);\n"
            );
        });

        // 3. Zapisz do PNG
        saveButton.setOnAction(e -> saveToPng(primaryStage));

        colorModeButton.setOnAction(e -> changeMode());

        // --- Lewa strona: Panel do wpisywania poleceń ---
        VBox controls = new VBox(10);
        codeInput = new TextArea();
        codeInput.setPromptText("Wprowadź kod GeoLang tutaj...");
        codeInput.setPrefWidth(300);
        codeInput.setPrefHeight(500);

        Button runButton = new Button("Uruchom i Rysuj");
        runButton.setMaxWidth(Double.MAX_VALUE);
        runButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        runButton.setOnAction(e -> processCode(codeInput.getText()));

        controls.getChildren().addAll(codeInput, runButton);
        root.setLeft(controls);

        // --- Prawa strona: Płótno ---
        canvas = new Canvas(800, 600);
        root.setCenter(canvas);

        // Inicjalne czyszczenie tła
        draw(canvas.getGraphicsContext2D());

        Scene scene = new Scene(root, 1150, 650);
        primaryStage.setTitle("GeoLang Interactive Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void changeMode() {
        isDarkMode = !isDarkMode; // Przełączamy tryb

        // Pobieramy główny kontener (root) z sceny
        BorderPane root = (BorderPane) canvas.getScene().getRoot();

        if (isDarkMode) {
            // Styl Ciemny
            root.setStyle("-fx-background-color: #2b2b2b;");
            codeInput.setStyle("-fx-control-inner-background: #3c3f41; -fx-text-fill: #a9b7c6;");
//            errorConsole.setStyle("-fx-control-inner-background: #3c3f41; -fx-text-fill: #ff6b68;");
        } else {
            // Styl Jasny
            root.setStyle("-fx-background-color: #f4f4f4;");
            codeInput.setStyle("-fx-control-inner-background: white; -fx-text-fill: black;");
//            errorConsole.setStyle("-fx-control-inner-background: white; -fx-text-fill: red;");
        }

        // NAJWAŻNIEJSZE: Przerysuj Canvas w nowym trybie
        draw(canvas.getGraphicsContext2D());
    }
    private void processCode(String code) {
        try {
            // 1. Parsowanie kodu z TextArea
            CharStream input = CharStreams.fromString(code);
            GeoLangLexer lexer = new GeoLangLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GeoLangParser parser = new GeoLangParser(tokens);

            parser.removeErrorListeners();
            parser.addErrorListener(new DescriptiveErrorListener());

            ParseTree tree = parser.program();

            // 2. Interpretacja
            Kolorowy visitor = new Kolorowy();
            visitor.visit(tree);

            // 3. Konwersja na figury (używamy metody ze Start.java)
            Map<String, VarType> variables = visitor.getAllVariables();
            this.figures = convertToDrawables(variables);

            // 4. Odświeżenie Canvasu
            draw(canvas.getGraphicsContext2D());

        } catch (Exception e) {
            System.err.println("Błąd interpretacji: " + e.getMessage());
            // Tu można by dodać okno z błędem dla użytkownika
        }
    }

    private void draw(GraphicsContext gc) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Czyścimy tło
        gc.clearRect(0, 0, width, height);
        if (isDarkMode) {
            gc.setFill(Color.valueOf("#1e1e1e")); // Ciemne tło canvasu
        } else {
            gc.setFill(Color.WHITE); // Jasne tło canvasu
        }
        gc.fillRect(0, 0, width, height);

        // Stan rysowania (reset transformacji)
        gc.save();

        // Odwrócenie osi Y – (0,0) lewy dolny róg (zgodnie z kodem kolegi)
        gc.translate(0, height);
        gc.scale(1, -1);

        if (isDarkMode) {
            gc.setStroke(Color.WHITE);
            gc.setFill(Color.WHITE);
        } else {
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.BLACK);
        }

        gc.setLineWidth(2.0);
        if (figures != null) {
            for (Drawable d : figures) {
                d.draw(gc);
            }
        }

        gc.restore(); // Przywracamy stan, żeby UI nie zwariowało
    }

    // Opcjonalna metoda do automatycznego dopasowania widoku
    private void applyAutoScale(GraphicsContext gc, double width, double height) {
        // Przykład: jeśli chcemy, aby cała zawartość była widoczna z marginesem
        double margin = 20;
        double contentWidth = maxX - minX;
        double contentHeight = maxY - minY;
        if (contentWidth > 0 && contentHeight > 0) {
            double scaleX = (width - 2 * margin) / contentWidth;
            double scaleY = (height - 2 * margin) / contentHeight;
            double scale = Math.min(scaleX, scaleY);
            double offsetX = -minX * scale + margin;
            double offsetY = -minY * scale + margin;

            gc.translate(offsetX, offsetY);
            gc.scale(scale, scale);
        }
    }
    private void saveToPng(Stage stage) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Zapisz rysunek jako PNG");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Obrazy PNG", "*.png")
        );

        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                // Robimy zdjęcie canvasu
                javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(
                        (int) canvas.getWidth(), (int) canvas.getHeight()
                );
                canvas.snapshot(null, writableImage);

                // Zapisujemy do pliku
                javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null);
                javax.imageio.ImageIO.write(
                        javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null),
                        "png",
                        file
                );

                showInfoDialog("Obraz został pomyślnie zapisany!");
            } catch (java.io.IOException ex) {

//                errorConsole.appendText("Błąd zapisu pliku: " + ex.getMessage() + "\n");
            }
        }
    }
}