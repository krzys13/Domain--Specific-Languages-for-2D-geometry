package interpreter;

import SymbolTable.LocalSymbols;
import grammar.GeoLangLexer;
import grammar.GeoLangParser;
import grammar.GeoLangParserBaseVisitor;
import interpreter.variables.*;
import java.util.*;

class Kolorowy extends GeoLangParserBaseVisitor<VarType> {

    private final LocalSymbols<VarType> variableMemory = new LocalSymbols<>();
    private final Map<String, FunctionDef> functions = new HashMap<>();
    private final Deque<String> callStack = new ArrayDeque<>();

    private VarType getDeclaredVariable(String name) {
        return variableMemory.getSymbol(name);
    }

    private VarType getInitializedVariable(String name) {
        VarType value = getDeclaredVariable(name);
        if (value instanceof UninitializedVarType) {
            throw new RuntimeException("Variable not initialized: " + name);
        }
        return value;
    }

    private VarType deepCopy(VarType value) {
        return switch (value.getType()) {
            case FLOAT -> new FloatType(asFloat(value).value);
            case POINT -> {
                PointType point = asPoint(value);
                yield new PointType(
                        (FloatType) deepCopy(point.x),
                        (FloatType) deepCopy(point.y)
                );
            }
            case LINE -> {
                LineType line = asLine(value);
                yield new LineType(
                        (PointType) deepCopy(line.p1),
                        (PointType) deepCopy(line.p2)
                );
            }
            case CIRCLE -> {
                CircleType circle = asCircle(value);
                yield new CircleType(
                        (PointType) deepCopy(circle.c),
                        (FloatType) deepCopy(circle.r)
                );
            }
        };
    }

    private VarTypeEnum mapContextType(GeoLangParser.TypeContext ctx) {
        return mapType(ctx.getText());
    }

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
    public VarType visitProgram(GeoLangParser.ProgramContext ctx) {
        for (GeoLangParser.StatContext statContext : ctx.stat()) {
            if (statContext instanceof GeoLangParser.Func_def_statContext funcDefStatContext) {
                visit(funcDefStatContext.func_def());
            }
        }

        VarType lastValue = null;
        for (GeoLangParser.StatContext statContext : ctx.stat()) {
            if (!(statContext instanceof GeoLangParser.Func_def_statContext)) {
                lastValue = visit(statContext);
            }
        }
        return lastValue;
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
            value = deepCopy(visit(ctx.expr()));

            if (value.getType() != declaredType) {
                throw new RuntimeException(
                        "Type mismatch for variable " + name +
                                ": expected " + declaredType +
                                ", got " + value.getType()
                );
            }
        } else {
            value = new UninitializedVarType(declaredType);
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
    public VarType visitFunc_def(GeoLangParser.Func_defContext ctx) {
        String name = ctx.ID().getText();
        if (!callStack.isEmpty()) {
            throw new RuntimeException("Function definitions inside functions are not allowed: " + name);
        }
        if (functions.containsKey(name)) {
            throw new RuntimeException("Function already declared: " + name);
        }

        List<VarTypeEnum> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        if (ctx.param_list() != null) {
            for (GeoLangParser.ParamContext paramCtx : ctx.param_list().param()) {
                String paramName = paramCtx.ID().getText();
                if (paramNames.contains(paramName)) {
                    throw new RuntimeException("Duplicate parameter name: " + paramName);
                }
                paramNames.add(paramName);
                paramTypes.add(mapContextType(paramCtx.type()));
            }
        }

        functions.put(name, new FunctionDef(
                mapContextType(ctx.type()),
                paramTypes,
                paramNames,
                ctx.block()
        ));
        return null;
    }

    @Override
    public VarType visitAssign(GeoLangParser.AssignContext ctx) {
        VarType value = deepCopy(visit(ctx.expr()));

        if (ctx.ID() != null) {
            String name = ctx.ID().getText();
            VarType current = getDeclaredVariable(name);

            if (current.getType() != value.getType()) {
                throw new RuntimeException(
                        "Incorrect type assignment: expected " +
                                current.getType() + ", got " + value.getType()
                );
            }

            variableMemory.setSymbol(name, value);
        } else {
            // field assignment, e.g. p.x = 5.0
            GeoLangParser.FieldContext field = ctx.field();
            String baseName = field.ID(0).getText();
            VarType obj = getInitializedVariable(baseName);

            // navigate to the parent of the last field
            for (int i = 1; i < field.ID().size() - 1; i++) {
                obj = obj.getField(field.ID(i).getText());
            }

            String lastField = field.ID(field.ID().size() - 1).getText();
            VarType current = obj.getField(lastField);//current value

            if (current.getType() != value.getType()) {
                throw new RuntimeException(
                        "Incorrect type assignment: expected " +
                                current.getType() + ", got " + value.getType()
                );
            }

            obj.setField(lastField, value);
        }

        return value;
    }


    @Override
    public VarType visitId_expr(GeoLangParser.Id_exprContext ctx) {
        String name = ctx.ID().getText();
        return getInitializedVariable(name);
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

    @Override
    public VarType visitUnary_minus_expr(GeoLangParser.Unary_minus_exprContext ctx) {
        FloatType value = asFloat(visit(ctx.expr()));
        return new FloatType(-value.value);
    }





    @Override public VarType visitValue(GeoLangParser.ValueContext ctx) {
        if (ctx.FLOAT_VALUE() != null)
            return new FloatType(Float.parseFloat(ctx.FLOAT_VALUE().getText()));
        else return visit(ctx.geo_value());
    }

    @Override
    public VarType visitMethod(GeoLangParser.MethodContext ctx) {
         String baseName = ctx.ID(0).getText();
         String methodName = ctx.ID().getLast().getText();
        VarType[] visitedArgs = ctx.expr().stream()
                .map(this::visit)
                .toArray(VarType[]::new);
         VarType currentObj = getInitializedVariable(baseName);

         // navigate to parent of method
         for ( int i = 1; i < ctx.ID().size()-1; i++){
             currentObj = currentObj.getField(ctx.ID(i).getText());
         }
         return (currentObj.getMethod(methodName,visitedArgs));

    }

    @Override
    public VarType visitFunc_call(GeoLangParser.Func_callContext ctx) {
        String functionName = ctx.ID().getText();
        FunctionDef function = functions.get(functionName);

        if (function == null) {
            throw new RuntimeException("Unknown function: " + functionName);
        }
        if (callStack.contains(functionName)) {
            throw new RuntimeException("Recursion is not allowed: " + functionName);
        }
        if (ctx.expr().size() != function.paramTypes().size()) {
            throw new RuntimeException("Function " + functionName + " expects " +
                    function.paramTypes().size() + " arguments, got " + ctx.expr().size());
        }

        List<VarType> arguments = new ArrayList<>();
        for (int i = 0; i < ctx.expr().size(); i++) {
            VarType argument = deepCopy(visit(ctx.expr(i)));
            VarTypeEnum expectedType = function.paramTypes().get(i);
            if (argument.getType() != expectedType) {
                throw new RuntimeException("Type mismatch for argument " + (i + 1) +
                        " in function " + functionName + ": expected " + expectedType +
                        ", got " + argument.getType());
            }
            arguments.add(argument);
        }

        callStack.push(functionName);
        variableMemory.enterScope();
        try {
            for (int i = 0; i < arguments.size(); i++) {
                String paramName = function.paramNames().get(i);
                variableMemory.newSymbol(paramName);
                variableMemory.setSymbol(paramName, arguments.get(i));
            }

            visit(function.body());
        } catch (ReturnSignal signal) {
            if (signal.value().getType() != function.returnType()) {
                throw new RuntimeException("Function " + functionName + " should return " +
                        function.returnType() + ", got " + signal.value().getType());
            }
            return deepCopy(signal.value());
        } finally {
            variableMemory.leaveScope();
            callStack.pop();
        }

        throw new RuntimeException("Function " + functionName + " must return a value");
    }

    @Override
    public VarType visitBlock(GeoLangParser.BlockContext ctx) {
        VarType lastValue = null;
        for (GeoLangParser.StatContext statContext : ctx.stat()) {
            lastValue = visit(statContext);
        }
        return lastValue;
    }

    @Override
    public VarType visitReturn_stat(GeoLangParser.Return_statContext ctx) {
        if (callStack.isEmpty()) {
            throw new RuntimeException("return outside of function");
        }
        throw new ReturnSignal(deepCopy(visit(ctx.expr())));
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
            VarType value = deepCopy(asPoint(getInitializedVariable(name)));
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
        VarType currentObj = getInitializedVariable(baseName);

        // navigate to the parent of the last field
        for (int i = 1; i <ctx.ID().size(); i++){
            currentObj =currentObj.getField(ctx.ID(i).getText());
        }
        return currentObj;
    }

    public Map<String, VarType> getAllVariables() {
        return variableMemory.getGlobalScope();
    }
}
