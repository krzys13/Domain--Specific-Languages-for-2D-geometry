package interpreter;


import SymbolTable.LocalSymbols;
import grammar.*;
import interpreter.variables.*;


class Kolorowy extends GeoLangParserBaseVisitor<Var>{

    final private LocalSymbols<Var> varaibleMemory = new LocalSymbols<>();
    @Override
    public Var visitDecl_stat(GeoLangParser.Decl_statContext ctx) {
        String name = ctx.decl().ID().getText();
        Var value = visit(ctx.decl().expr());
        System.out.println("LOG:Dodaj do hash mapy " + name + "=" + value);

        if (!varaibleMemory.hasSymbol(name) && varaibleMemory.hasSymbolDepth(name) == null) {
            varaibleMemory.newSymbol(name);
            varaibleMemory.setSymbol(name, value);
        } else {
            varaibleMemory.setSymbol(name, value);
        }
        return  null;
    }

//    @Override
//    public VarType visitMath_expr(GeoLangParser.Math_exprContext ctx) {
//        Float result;
//        switch (ctx.op.getType()){
//            case GeoLangLexer.ADD:
//                result = new Float(visit(ctx.l).value() +visit(ctx.r).value());
//
//            case GeoLangLexer.SUB:
//            case GeoLangLexer.MUL
//            case GeoLangLexer.DIV:
//
//
//        }
//    }


    @Override
    public Var visitFloat_num_expr(GeoLangParser.Float_num_exprContext ctx) {
        return new FloatType(Float.parseFloat(ctx.FLOAT().getText()));
    }


}