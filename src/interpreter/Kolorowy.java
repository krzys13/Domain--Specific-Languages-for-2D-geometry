package interpreter;


import SymbolTable.LocalSymbols;
import grammar.*;
import interpreter.variables.*;


class Kolorowy extends GeoLangParserBaseVisitor<VarType> {

    final private LocalSymbols<VarType> varaibleMemory = new LocalSymbols<>();

    private FloatType asFloat(VarType value) {
        if (value instanceof FloatType f) {
            return f;
        }
        throw new RuntimeException("Expected FLOAT, got: " + value.getType());
    }

    private PointType asPoint(VarType value) {
        if (value instanceof PointType p) {
            return p;
        }
        throw new RuntimeException("Expected POINT, got: " + value.getType());
    }

    private LineType asLine(VarType value) {
        if (value instanceof LineType l) {
            return l;
        }
        throw new RuntimeException("Expected LINE, got: " + value.getType());
    }

    private CircleType asCircle(VarType value) {
        if (value instanceof CircleType c) {
            return c;
        }
        throw new RuntimeException("Expected CIRCLE, got: " + value.getType());
    }




    @Override
    public VarType visitFloat_decl(GeoLangParser.Float_declContext ctx) {
        String name = ctx.ID().getText();
        VarType value = visit(ctx.expr()); //  zwraca obiekt typu FloatType

        if (varaibleMemory.hasSymbolDepth(name) == null) {
            varaibleMemory.newSymbol(name);
        }
        varaibleMemory.setSymbol(name, value);
        return null;
    }


    @Override
    public VarType visitGeo_decl(GeoLangParser.Geo_declContext ctx) {
        String name = ctx.ID().getText();
        VarType value;

        if (ctx.point_value() != null) {
            value = visit(ctx.point_value()); // zostanie zwrocony obeikt typy PoinType
        } else if (ctx.line_value() != null) {
            value = visit(ctx.line_value());
        } else if (ctx.circle_value() != null) {
            value = visit(ctx.circle_value());
        } else {
            throw new RuntimeException("Unknown geo declaration");
        }

        System.out.println("LOG:Dodaj do hash mapy " + name + "=" + value);

        if (varaibleMemory.hasSymbolDepth(name) == null) {
            varaibleMemory.newSymbol(name);
        }
        varaibleMemory.setSymbol(name, value);

        return null;
    }


    @Override
    public VarType visitFloat_assign(GeoLangParser.Float_assignContext ctx) {
        String name = ctx.ID().getText();

        VarType value = visit(ctx.expr());
        VarType currentValue = varaibleMemory.getSymbol(name);

        if (currentValue.getType() == value.getType()) { // sprawdzam czy zmienna w pamieci ma ten sam typ co to co chcemy do niej przypsiac
            varaibleMemory.setSymbol(name, value);
            return value;
        } else {
            throw new RuntimeException(
                    "Incorrect type assignment: expected " + currentValue.getType() + ", got " + value.getType()
            );
        }
    }


    @Override
    public VarType visitGeo_assign(GeoLangParser.Geo_assignContext ctx) {
        String name = ctx.ID().getText();

        VarType value = visit(ctx.geo_value());
        VarType currentValue = varaibleMemory.getSymbol(name);

        if(currentValue.getType() == value.getType()) { // sprawdzam czy zmienna w pamieci ma ten sam typ co to co chcemy do niej przypsiac
            varaibleMemory.setSymbol(name, value);
            return value;
        }
        else {
            throw new RuntimeException(
                    "Incorrect type assignment: expected " + currentValue.getType() + ", got " + value.getType()
            );
        }
    }

    @Override
    public VarType visitFloat_num_expr(GeoLangParser.Float_num_exprContext ctx) {
        return new FloatType(Float.parseFloat(ctx.FLOAT().getText()));
    }

    @Override
    public VarType visitPoint_value(GeoLangParser.Point_valueContext ctx) {

        FloatType left = asFloat(visit(ctx.l));
        FloatType right = asFloat(visit(ctx.r));
        return new PointType(left, right);
    }


    @Override
    public VarType visitLine_value(GeoLangParser.Line_valueContext ctx) {
        PointType p1 = asPoint(visit(ctx.l));
        PointType p2 = asPoint(visit(ctx.r));
        return new LineType(p1, p2);
    }


    @Override
    public VarType visitCircle_value(GeoLangParser.Circle_valueContext ctx) {
        PointType s = asPoint(visit(ctx.l));
        FloatType r = asFloat(visit(ctx.r));
        return new CircleType(s, r);


    }


    @Override
    public VarType visitId_expr(GeoLangParser.Id_exprContext ctx) {
        String name = ctx.ID().getText(); // nazwa zmiennej
        VarType value = varaibleMemory.getSymbol(name);
        return value;
    }
}

