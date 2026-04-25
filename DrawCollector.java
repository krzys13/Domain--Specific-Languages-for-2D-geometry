package interpreter.drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Globalny kontener na figury, które mają być narysowane.
 * Wywołanie {@code render()} na zmiennej geometrycznej dodaje tu odpowiedni obiekt {@link Drawable}.
 */
public class DrawCollector {
    private static final List<Drawable> drawQueue = new ArrayList<>();

    public static void add(Drawable drawable) {
        drawQueue.add(drawable);
    }

    public static List<Drawable> getDrawables() {
        return Collections.unmodifiableList(drawQueue);
    }

    /** Czyści listę (jeśli program byłby wykonywany wielokrotnie) */
    public static void clear() {
        drawQueue.clear();
    }
}