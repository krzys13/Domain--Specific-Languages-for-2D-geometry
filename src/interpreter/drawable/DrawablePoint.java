package interpreter.drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DrawablePoint implements Drawable {
    private final double x, y;
    private static final double SIZE = 4.0;

    public DrawablePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.fillOval(x - SIZE/2, y - SIZE/2, SIZE, SIZE);
    }
}