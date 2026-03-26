package interpreter;
import grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Start {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromString("point a = (1,1);");

        // create a lexer that feeds off of input CharStream
        GeoLangLexer lexer = new GeoLangLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        GeoLangParser parser = new GeoLangParser(tokens);

        // start parsing at the program rule
        ParseTree tree = parser.program();
        // System.out.println(tree.toStringTree(parser));

        // create a visitor to traverse the parse tree
        Kolorowy visitor = new Kolorowy();
        System.out.println(visitor.visit(tree));
    }
}