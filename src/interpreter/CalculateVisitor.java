package interpreter;


import SymbolTable.GlobalSymbols;
import SymbolTable.LocalSymbols;
import grammar.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;



public class CalculateVisitor extends firstBaseVisitor<Integer> {
    private TokenStream tokStream = null;
    private CharStream input = null;

    private final LocalSymbols<Integer> LocalSymbols = new LocalSymbols<>();

    public CalculateVisitor(CharStream inp) {
        super();
        this.input = inp;
    }

    public CalculateVisitor(TokenStream tok) {
        super();
        this.tokStream = tok;
    }

    public CalculateVisitor(CharStream inp, TokenStream tok) {
        super();
        this.input = inp;
        this.tokStream = tok;
    }

    private String getText(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        if (input == null) throw new RuntimeException("Input stream undefined");
        return input.getText(new Interval(a, b));
    }

    // Visitory bez implementacji wykonują te domyśle z interfejsu czyli iteruja w dół drzewa!!


    @Override
    public Integer visitIf_stat(firstParser.If_statContext ctx) {
        Integer result = 0;
        if (visit(ctx.cond) != 0) {
            result = visit(ctx.then);
        } else {
            if (ctx.else_ != null)
                result = visit(ctx.else_);
        }
        return result;
    }

    @Override
    public Integer visitPrint_stat(firstParser.Print_statContext ctx) {
        var st = ctx.expr();
        var result = visit(st);
        System.out.printf("|%s=%d|\n", st.getText(), result); //nie drukuje ukrytych ani pominiętych spacji
//        System.out.printf("|%s=%d|\n", getText(st),  result); //drukuje wszystkie spacje
//        System.out.printf("|%s=%d|\n", tokStream.getText(st),  result); //drukuje spacje z ukrytego kanału, ale nie ->skip
        return result;
    }
    @Override
    public Integer visitFor_stat(firstParser.For_statContext ctx) {
        Integer result = 0;

        visit(ctx.init);

        while (visit(ctx.cond) != 0) {
            result = visit(ctx.body);
            visit(ctx.step);
        }

        return result;
    }

    @Override
    public Integer visitBlock_real(firstParser.Block_realContext ctx) {
        LocalSymbols.enterScope();
        super.visitChildren(ctx);
        LocalSymbols.leaveScope();
        return null;
    }

    // Tutaj są poszczególne metody odwiedzania podzasad reguły expr
    @Override
    public Integer visitInt_tok(firstParser.Int_tokContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }

    @Override
    public Integer visitPars(firstParser.ParsContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Integer visitBinOp(firstParser.BinOpContext ctx) {
        Integer result = 0;
        switch (ctx.op.getType()) {
            case firstLexer.ADD:
                result = visit(ctx.l) + visit(ctx.r);
                break;
            case firstLexer.SUB:
                result = visit(ctx.l) - visit(ctx.r);
                break;
            case firstLexer.MUL:
                result = visit(ctx.l) * visit(ctx.r);
                break;
            case firstLexer.DIV:
                try {
                    result = visit(ctx.l) / visit(ctx.r);
                } catch (Exception e) {
                    System.err.println("Div by zero");
                    throw new ArithmeticException();
                }
        }
        return result;
    }

    @Override
    public Integer visitAssign(firstParser.AssignContext ctx) {
        String name = ctx.ID().getText();
        Integer value = visit(ctx.expr());
        System.out.println("LOG:Dodaj do hash mapy " + name + "=" + value);

        if (!LocalSymbols.hasSymbol(name) && LocalSymbols.hasSymbolDepth(name) == null) {
            LocalSymbols.newSymbol(name);
            LocalSymbols.setSymbol(name,value);
        } else {
            LocalSymbols.setSymbol(name,value);
        }

        return value;
    }

    public Integer visitId_tok(firstParser.Id_tokContext ctx) {
        String name = ctx.ID().getText();
        Integer value = LocalSymbols.getSymbol(name);
        System.out.println("LOG:Odczyutuje z hash mapy " + name);
        return value;
    }

    @Override
    public Integer visitRelOp(firstParser.RelOpContext ctx) {
        boolean result_boolen;

        switch (ctx.op.getType()) {
            case firstLexer.LESS_THAN:
                result_boolen = visit(ctx.l) < visit(ctx.r);
                break;

            case firstLexer.LESS_EQUAL_THAN:
                result_boolen = visit(ctx.l) <= visit(ctx.r);
                break;

            case firstLexer.MORE_THAN:
                result_boolen = visit(ctx.l) > visit(ctx.r);
                break;

            case firstLexer.MORE_EQUAL_THAN:
                result_boolen = visit(ctx.l) >= visit(ctx.r);
                break;

            default:
                throw new RuntimeException("Unknown relational operator: " + ctx.op.getText());
        }

        if (result_boolen == true) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public Integer visitEqOp(firstParser.EqOpContext ctx) {
        boolean result_boolen;

        switch (ctx.op.getType()) {
            case firstLexer.EQUAL:
                result_boolen = visit(ctx.l) == visit(ctx.r);
                break;

            case firstLexer.NOT_EQUAL:
                result_boolen = visit(ctx.l) != visit(ctx.r);
                break;

            default:
                throw new RuntimeException("Unknown equality operator: " + ctx.op.getText());
        }

        if (result_boolen == true) {
            return 1;
        } else {
            return 0;
        }

    }
    @Override
    public Integer visitAndOp(firstParser.AndOpContext ctx) {
        boolean result_boolen;

        if (visit(ctx.l) == 1 && visit(ctx.r) == 1) {
            result_boolen = true;
        }
        else {
            result_boolen = false;
        }

        if (result_boolen == true) {
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public Integer visitOrOp(firstParser.OrOpContext ctx) {
        boolean result_boolen;

        if (visit(ctx.l) != 0 || visit(ctx.r) != 0) {
            result_boolen = true;
        }
        else {
            result_boolen = false;
        }

        if (result_boolen == true) {
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public Integer visitNotOp(firstParser.NotOpContext ctx) {
        boolean result_boolen;

        if (visit(ctx.expr()) == 0) {
            result_boolen = true;
        }
        else {
            result_boolen = false;
        }

        if (result_boolen == true) {
            return 1;
        }
        else {
            return 0;
        }
    }






}


