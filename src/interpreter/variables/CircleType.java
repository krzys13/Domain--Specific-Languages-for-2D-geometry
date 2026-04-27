package interpreter.variables;

import interpreter.drawable.DrawCollector;
import interpreter.drawable.DrawableCircle;

public class CircleType implements VarType {

    private final VarTypeEnum type;

    public PointType c;// center of cirlce
    public FloatType r; // radius

    public CircleType(PointType s, FloatType r) {
        this.type = VarTypeEnum.CIRCLE;
        this.c = s;
        this.r = r;
    }

    private static PointType rotatePoint(PointType point, PointType pivot, float angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);
        double translatedX = point.x.value - pivot.x.value;
        double translatedY = point.y.value - pivot.y.value;

        double rotatedX = translatedX * Math.cos(angleRadians) - translatedY * Math.sin(angleRadians);
        double rotatedY = translatedX * Math.sin(angleRadians) + translatedY * Math.cos(angleRadians);

        return new PointType(
                new FloatType((float) (rotatedX + pivot.x.value)),
                new FloatType((float) (rotatedY + pivot.y.value))
        );
    }

    @Override
    public VarTypeEnum getType() {
        return this.type;
    }

    @Override
    public VarType getField(String fieldName) {
        switch (fieldName) {
            case "c":
                return (this.c);
            case "r":
                return (this.r);
            default:
                throw new RuntimeException("CIRCLE has no field: " + fieldName);
        }
    }

    @Override
    public VarType setField(String fieldName, VarType value) {
        switch (fieldName) {
            case "c": return this.c = (PointType) value;
            case "r": return this.r = (FloatType) value;
            default: throw new RuntimeException("CIRCLE has no field: " + fieldName);
        }
    }

    @Override
    public VarType getMethod(String methodName, VarType... args) {
        return switch (methodName) {
            case "render" -> {
                DrawCollector.add(new DrawableCircle(
                        this.c.x.value, this.c.y.value, this.r.value));
                yield this;
            }
            case "move" -> {
                if (args.length != 2) {
                    throw new RuntimeException("CIRCLE.move expects 2 arguments");
                }
                if (!(args[0] instanceof FloatType dx) || !(args[1] instanceof FloatType dy)) {
                    throw new RuntimeException("CIRCLE.move expects FLOAT, FLOAT arguments");
                }
                this.c = new PointType(
                        new FloatType(this.c.x.value + dx.value),
                        new FloatType(this.c.y.value + dy.value)
                );
                yield this;
            }
            case "rotate" -> {
                if (args.length != 1 && args.length != 2) {
                    throw new RuntimeException("CIRCLE.rotate expects 1 or 2 arguments");
                }
                if (!(args[0] instanceof FloatType angle)) {
                    throw new RuntimeException("CIRCLE.rotate expects FLOAT angle as first argument");
                }
                if (args.length == 2) {
                    if (!(args[1] instanceof PointType pivot)) {
                        throw new RuntimeException("CIRCLE.rotate expects POINT as second argument");
                    }
                    this.c = rotatePoint(this.c, pivot, angle.value);
                }
                yield this;
            }
            default -> throw new RuntimeException("CIRCLE has no method: " + methodName);
        };
    }

    @Override
    public String toString() {
        return "(" + c + ", " + r + ")";
    }
}
