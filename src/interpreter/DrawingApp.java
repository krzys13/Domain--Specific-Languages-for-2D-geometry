package interpreter;

import interpreter.drawable.Drawable;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class DrawingApp extends Application {
    private static List<Drawable> figures;
    private static double minX = Double.POSITIVE_INFINITY;
    private static double maxX = Double.NEGATIVE_INFINITY;
    private static double minY = Double.POSITIVE_INFINITY;
    private static double maxY = Double.NEGATIVE_INFINITY;

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
        if (figures == null || figures.isEmpty()) {
            showInfoDialog("Brak figur do narysowania.");
        }

        // Ustalamy rozmiar sceny
        double canvasWidth = 800;
        double canvasHeight = 600;
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Odwrócenie osi Y – teraz (0,0) to lewy dolny róg
        gc.translate(0, canvasHeight);
        gc.scale(1, -1);

        // Automatyczne skalowanie (opcjonalne – patrz opis poniżej)
        // applyAutoScale(gc, canvasWidth, canvasHeight);

        // Ustawienia domyślne
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.setFill(Color.BLACK);

        // Rysowanie wszystkich figur
        for (Drawable d : figures) {
            d.draw(gc);
        }

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, canvasWidth, canvasHeight);
        primaryStage.setTitle("GeoLang Viewer");
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
}