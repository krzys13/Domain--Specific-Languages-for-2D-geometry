package interpreter;

import javafx.scene.control.TextArea;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

// Pomocnicza klasa do wyświetlania błędów składniowych
public class DescriptiveErrorListener extends BaseErrorListener {
    private final TextArea output;

    public DescriptiveErrorListener(TextArea output) {
        this.output = output;
    }
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        String errorMsg = String.format("Linia %d:%d - %s\n", line, charPositionInLine, msg);

        output.appendText(errorMsg);
    }
}