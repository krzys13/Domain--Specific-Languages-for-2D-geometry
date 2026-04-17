package interpreter.drawable;
import javafx.scene.canvas.GraphicsContext;

public class DrawableCircle implements Drawable {
    private final double centerX, centerY, radius;

    public DrawableCircle(double centerX, double centerY, double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }
}