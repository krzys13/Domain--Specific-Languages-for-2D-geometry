package interpreter.variables;

import interpreter.drawable.DrawCollector;
import interpreter.drawable.DrawablePoint;

public class PointType implements VarType {

    private final VarTypeEnum type;
    public FloatType x, y;

    public PointType(FloatType x, FloatType y)
    {
        this.type = VarTypeEnum.POINT;
        this.x = x;
        this.y = y;

    }
    @Override
    public VarTypeEnum getType() {
        return this.type;
    }

    @Override
    public VarType getField(String fieldName) {
        switch (fieldName) {
            case "x":
                return (this.x);
            case "y":
                return (this.y);
            default:
                throw new RuntimeException("POINT has no field: " + fieldName);
        }
    }

    @Override
    public VarType setField(String fieldName, VarType value) {
        switch (fieldName) {
            case "x": return this.x = (FloatType) value;
            case "y": return this.y = (FloatType) value;
            default: throw new RuntimeException("POINT has no field: " + fieldName);
        }
    }

    @Override
    public VarType getMethod(String methodName, VarType... args) {
        return switch (methodName) {
            case "render" -> {
                DrawCollector.add(new DrawablePoint(this.x.value, this.y.value));
                yield this;
            }
            case "move" -> {
                if (args.length != 2) {
                    throw new RuntimeException("POINT.move expects 2 arguments");
                }
                if (!(args[0] instanceof FloatType dx) || !(args[1] instanceof FloatType dy)) {
                    throw new RuntimeException("POINT.move expects FLOAT, FLOAT arguments");
                }
                this.x = new FloatType(this.x.value + dx.value);
                this.y = new FloatType(this.y.value + dy.value);
                yield this;
            }
            default -> throw new RuntimeException("POINT has no method: " + methodName);
        };
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
