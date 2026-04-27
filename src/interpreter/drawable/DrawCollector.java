package interpreter.drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawCollector {
    private static final List<Drawable> drawQueue = new ArrayList<>();

    public static void add(Drawable drawable) {
        drawQueue.add(drawable);
    }

    public static List<Drawable> getDrawables() {
        return Collections.unmodifiableList(drawQueue);
    }

    public static void clear() {
        drawQueue.clear();
    }
}
