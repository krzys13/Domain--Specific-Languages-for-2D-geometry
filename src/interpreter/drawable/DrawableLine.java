package interpreter.drawable;
import javafx.scene.canvas.GraphicsContext;

public class DrawableLine implements Drawable {
    private final double x1, y1, x2, y2;

    public DrawableLine(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.strokeLine(x1, y1, x2, y2);
    }
}