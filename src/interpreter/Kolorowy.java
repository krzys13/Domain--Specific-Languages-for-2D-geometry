package interpreter;

import SymbolTable.LocalSymbols;
import grammar.GeoLangLexer;
import grammar.GeoLangParser;
import grammar.GeoLangParserBaseVisitor;
import interpreter.variables.*;

class Kolorowy extends GeoLangParserBaseVisitor<VarType> {

    private final LocalSymbols<VarType> variableMemory = new LocalSymbols<>();

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

    private VarTypeEnum mapType(String type) {
        return switch (type) {
            case "float" -> VarTypeEnum.FLOAT;
            case "point" -> VarTypeEnum.POINT;
            case "line" -> VarTypeEnum.LINE;
            case "circle" -> VarTypeEnum.CIRCLE;
            default -> throw new RuntimeException("Unknown type: " + type);
        };
    }

    @Override
    public VarType visitDecl(GeoLangParser.DeclContext ctx) {
        String name = ctx.ID().getText();
        VarTypeEnum declaredType = mapType(ctx.type().getText());

        if (variableMemory.hasSymbolDepth(name) != null) {
            throw new RuntimeException("Variable already declared: " + name);
        }

        VarType value;

        if (ctx.expr() != null) {
            value = visit(ctx.expr());

            if (value.getType() != declaredType) {
                throw new RuntimeException(
                        "Type mismatch for variable " + name +
                                ": expected " + declaredType +
                                ", got " + value.getType()
                );
            }
        } else {
            if (declaredType == VarTypeEnum.FLOAT) {
                value = new FloatType(0.0f);
            } else {
                throw new RuntimeException("Variable " + name + " must be initialized");
            }
        }

        variableMemory.newSymbol(name);
        variableMemory.setSymbol(name, value);
        return value;
    }

    @Override
    public VarType visitPrint_stat(GeoLangParser.Print_statContext ctx) {
        VarType value = visit(ctx.expr());
        System.out.println(value);
        return value;
    }

    @Override
    public VarType visitAssign(GeoLangParser.AssignContext ctx) {

        String name = ctx.ID().getText();
        VarType value = visit(ctx.expr());

        VarType current = variableMemory.getSymbol(name);
        if (current == null) {
            throw new RuntimeException("Undeclared variable: " + name);
        }

        if (current.getType() != value.getType()) {
            throw new RuntimeException(
                    "Incorrect type assignment: expected " +
                            current.getType() + ", got " + value.getType()
            );
        }

        variableMemory.setSymbol(name, value);
        return value;
    }


    @Override
    public VarType visitId_expr(GeoLangParser.Id_exprContext ctx) {
        String name = ctx.ID().getText();
        VarType value = variableMemory.getSymbol(name);

        if (value == null) {
            throw new RuntimeException("Undeclared variable: " + name);
        }

        return value;
    }



    @Override
    public VarType visitMath_expr(GeoLangParser.Math_exprContext ctx) {
        FloatType left = asFloat(visit(ctx.l));
        FloatType right = asFloat(visit(ctx.r));

        return switch (ctx.op.getType()) {
            case GeoLangLexer.ADD -> new FloatType(left.value + right.value);
            case GeoLangLexer.SUB -> new FloatType(left.value - right.value);
            case GeoLangLexer.MUL -> new FloatType(left.value * right.value);
            case GeoLangLexer.DIV -> {
                if (right.value == 0) {
                    throw new RuntimeException("Division by zero");
                }
                yield new FloatType(left.value / right.value);
            }
            default -> throw new RuntimeException("Unknown math operator: " + ctx.op.getText());
        };
    }





    @Override public VarType visitValue(GeoLangParser.ValueContext ctx) {
        if (ctx.FLOAT_VALUE() != null)
            return new FloatType(Float.parseFloat(ctx.FLOAT_VALUE().getText()));
        else return visit(ctx.geo_value());
    }



    @Override
    public VarType visitPoint_value(GeoLangParser.Point_valueContext ctx) {
        FloatType left = asFloat(visit(ctx.l));
        FloatType right = asFloat(visit(ctx.r));
        return new PointType(left, right);
    }


    @Override
    public VarType visitPoint_ref(GeoLangParser.Point_refContext ctx) {
        if (ctx.ID() != null) {
            String name = ctx.ID().getText();
            VarType value = asPoint(variableMemory.getSymbol(name));
            return value;
        }

        return visit(ctx.point_value());
    }



    @Override
    public VarType visitLine_value(GeoLangParser.Line_valueContext ctx) {
        PointType p1 = asPoint(visit(ctx.l));
        PointType p2 = asPoint(visit(ctx.r));
        return new LineType(p1, p2);
    }

    @Override
    public VarType visitCircle_value(GeoLangParser.Circle_valueContext ctx) {
        PointType center = asPoint(visit(ctx.l));
        FloatType radius = asFloat(visit(ctx.r));
        return new CircleType(center, radius);
    }

    @Override
    public VarType visitField(GeoLangParser.FieldContext ctx) {
        String baseName = ctx.ID(0).getText();
        VarType currentObject = variableMemory.getSymbol(baseName);

        for (int i = 1; i <ctx.ID().size(); i++){
            currentObject =currentObject.getField(ctx.ID(i).getText());
        }
        return currentObject;
    }
}