package interpreter;

import grammar.*;
import interpreter.drawable.DrawCollector;
import interpreter.drawable.Drawable;
import javafx.application.Application;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;

public class Start {
    public static void main(String[] args) {
        try {
            // 1. Parsowanie
            CharStream input = CharStreams.fromFileName("we.GeoLang");
            GeoLangLexer lexer = new GeoLangLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GeoLangParser parser = new GeoLangParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new DescriptiveErrorListener());
            ParseTree tree = parser.program();

            // 2. Interpretacja (wykonanie skryptu, w tym wywołania render())
            Kolorowy visitor = new Kolorowy();
            visitor.visit(tree);

            // 3. Pobranie figur, które zostały jawnie wyrenderowane
            List<Drawable> figures = DrawCollector.getDrawables();

            // 4. JavaFX
            DrawingApp.setFigures(figures);
            Application.launch(DrawingApp.class, args);

        } catch (Exception e) {
            System.err.println("Błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

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