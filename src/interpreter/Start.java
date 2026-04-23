package interpreter;

import grammar.*;
import interpreter.drawable.*;
import interpreter.variables.CircleType;
import interpreter.variables.LineType;
import interpreter.variables.PointType;
import interpreter.variables.VarType;
import javafx.application.Application;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Start {
    public static void main(String[] args) {
        try {
            // 1. Parsowanie pliku źródłowego
            CharStream input = CharStreams.fromFileName("we.GeoLang");
            GeoLangLexer lexer = new GeoLangLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GeoLangParser parser = new GeoLangParser(tokens);

            // (Opcjonalnie) dodaj własny ErrorListener, aby ładnie wyświetlać błędy
            parser.removeErrorListeners();
            parser.addErrorListener(new DescriptiveErrorListener());

            ParseTree tree = parser.program();

            // 2. Interpretacja (wykonanie kodu)
            Kolorowy visitor = new Kolorowy();
            visitor.visit(tree);

            // 3. Pobranie figur
            Map<String, VarType> variables = visitor.getAllVariables();
            List<Drawable> figures = convertToDrawables(variables);

            // 4. Uruchomienie JavaFX
            DrawingApp.setFigures(figures);
            Application.launch(DrawingApp.class, args);

        } catch (Exception e) {
            System.err.println("Błąd: " + e.getMessage());
            e.printStackTrace();
            // Możesz tu też wyświetlić okno dialogowe JavaFX, ale launch jeszcze nie działa
        }
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
}

// Pomocnicza klasa do wyświetlania błędów składniowych
class DescriptiveErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        String sourceName = recognizer.getInputStream().getSourceName();
        if (!sourceName.isEmpty()) {
            sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
        }
        System.err.println(sourceName + "błąd składni: " + msg);
    }
}