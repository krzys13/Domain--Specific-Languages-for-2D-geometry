package interpreter;

import grammar.GeoLangLexer;
import grammar.GeoLangParser;
import interpreter.drawable.Drawable;
import interpreter.drawable.DrawCollector;
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

public class DrawingApp extends Application {
    private static List<Drawable> figures = new ArrayList<>();
    private Canvas canvas;
    private TextArea codeInput;
    private static double minX = Double.POSITIVE_INFINITY;
    private static double maxX = Double.NEGATIVE_INFINITY;
    private static double minY = Double.POSITIVE_INFINITY;
    private static double maxY = Double.NEGATIVE_INFINITY;
    private boolean isDarkMode = false;
    private TextArea errorConsole;

    public static void setFigures(List<Drawable> figs) {
        figures = figs;
        computeBounds(figs);
    }

    private static void computeBounds(List<Drawable> figs) {
        // Dla uproszczenia zakładamy, że każdy Drawable potrafi podać swoje granice
        // (można rozszerzyć interfejs Drawable o metodę bounds())
        // Tutaj pomijam – w zaawansowanej wersji można dodać.
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- Pasek narzędzi na górze ---
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(0, 0, 10, 0));

        Button clearButton = new Button("Wyczyść");
        clearButton.setId("btn-clear");

        Button exampleButton = new Button("Przykład");
        exampleButton.setId("btn-example");

        Button saveButton = new Button("Zapisz PNG");
        saveButton.setId("btn-save");

        Button colorModeButton = new Button("Dark/Light");
        colorModeButton.setId("btn-mode");


        toolbar.getChildren().addAll(exampleButton, clearButton, saveButton,  colorModeButton);
        root.setTop(toolbar);


        // --- Lewa strona: Panel do wpisywania poleceń ---
        VBox controls = new VBox(10);
        codeInput = new TextArea();
        codeInput.setPromptText("Wprowadź kod GeoLang tutaj...");
        codeInput.setPrefWidth(300);
        codeInput.setPrefHeight(500);

        Button runButton = new Button("Uruchom i Rysuj");
        runButton.setId("btn-run");
        runButton.setMaxWidth(Double.MAX_VALUE);
        runButton.setOnAction(e -> processCode(codeInput.getText()));

        errorConsole = new TextArea();
        errorConsole.setEditable(false);
        errorConsole.setPrefHeight(150);
        errorConsole.setId("error-console");

        controls.getChildren().addAll(codeInput, runButton, errorConsole);
        root.setLeft(controls);

        // --- Prawa strona: Płótno ---
        canvas = new Canvas(800, 600);
        root.setCenter(canvas);


        // --- Akcje dla przycisków ---

        // 1. Wyczyść
        clearButton.setOnAction(e -> {
            codeInput.clear();
            errorConsole.clear();
            DrawCollector.clear();
            figures.clear();
            draw(canvas.getGraphicsContext2D());
        });

        // 2. Przykładowe dane
        exampleButton.setOnAction(e -> {
            codeInput.setText(
                    """
                            point a = (100, 100);
                            point b = (300, 300);
                            point d = (500, 200);
                            line l = (a, b);
                            circle c1 = (d, 50);
                            circle c2 = (d, 100);
                            """
            );
        });

        // 3. Zapisz do PNG
        saveButton.setOnAction(e -> saveToPng(primaryStage));

        // 4. Zmiana trybu jasny/ciemny
        colorModeButton.setOnAction(e -> changeMode());

        // Inicjalne czyszczenie tła
        draw(canvas.getGraphicsContext2D());

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
        root.getStyleClass().add("root-light");
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
        isDarkMode = !isDarkMode;
        BorderPane root = (BorderPane) canvas.getScene().getRoot();

        root.getStyleClass().removeAll("root-dark", "root-light");
        if (isDarkMode) {
            root.getStyleClass().add("root-dark");
        } else {
            root.getStyleClass().add("root-light");
        }

        draw(canvas.getGraphicsContext2D());
    }

    private void processCode(String code) {
        errorConsole.clear();
        DrawCollector.clear();
        figures.clear();

        try {
            // 1. Parsowanie kodu z TextArea
            CharStream input = CharStreams.fromString(code);
            GeoLangLexer lexer = new GeoLangLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GeoLangParser parser = new GeoLangParser(tokens);

            parser.removeErrorListeners();
            parser.addErrorListener(new DescriptiveErrorListener(errorConsole));

            ParseTree tree = parser.program();

            // 2. Interpretacja
            Kolorowy visitor = new Kolorowy();
            visitor.visit(tree);

            // 3. Pobranie figur jawnie wyrenderowanych przez render()
            figures = new ArrayList<>(DrawCollector.getDrawables());

            // 4. Odświeżenie Canvasu
            draw(canvas.getGraphicsContext2D());

        } catch (Exception e) {
            errorConsole.appendText("Błąd interpretacji: " + e.getMessage() + "\n");        }
    }

    private void draw(GraphicsContext gc) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Czyścimy tło
        gc.clearRect(0, 0, width, height);
        if (isDarkMode) {
            gc.setFill(Color.valueOf("#1e1e1e"));
        } else {
            gc.setFill(Color.WHITE);
        }
        gc.fillRect(0, 0, width, height);

        drawGrid(gc, width, height, 50);

        // Stan rysowania (reset transformacji)
        gc.save();

        // Odwrócenie osi Y – (0,0) lewy dolny róg
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

        gc.restore(); // Przywracamy stan
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
                javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(
                        (int) canvas.getWidth(), (int) canvas.getHeight()
                );
                canvas.snapshot(null, writableImage);

                javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null);
                javax.imageio.ImageIO.write(
                        javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null),
                        "png",
                        file
                );

                showInfoDialog("Obraz został pomyślnie zapisany!");
            } catch (java.io.IOException ex) {

                errorConsole.appendText("Błąd zapisu pliku: " + ex.getMessage() + "\n");
            }
        }
    }
    private void drawGrid(GraphicsContext gc, double width, double height, int spacing) {
        gc.save();

        if (isDarkMode) {
            gc.setStroke(Color.web("#333333"));
            gc.setFill(Color.web("#777777"));
        } else {
            gc.setStroke(Color.web("#E0E0E0"));
            gc.setFill(Color.web("#AAAAAA"));
        }

        gc.setLineWidth(1.0);
        gc.setFont(new javafx.scene.text.Font("Arial", 10));

        for (int x = 0; x <= width; x += spacing) {
            gc.strokeLine(x, 0, x, height);
            if (x > 0) {
                gc.fillText(String.valueOf(x), x + 2, height - 2);
            }
        }

        for (int y = 0; y <= height; y += spacing) {
            gc.strokeLine(0, y, width, y);
            if (y > 0) {
                int mathY = (int) (height - y);
                gc.fillText(String.valueOf(mathY), 2, y - 2);
            }
        }

        gc.restore();
    }
}
