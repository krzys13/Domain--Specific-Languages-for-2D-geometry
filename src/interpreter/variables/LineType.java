package interpreter.variables;

import interpreter.drawable.DrawCollector;
import interpreter.drawable.DrawableLine;

public class LineType implements VarType {
    private final VarTypeEnum type;

    public PointType p1, p2;

    public LineType(PointType p1, PointType p2) {
        this.type = VarTypeEnum.LINE;
        this.p1 = p1;
        this.p2 = p2;

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

    private PointType getCenter() {
        return new PointType(
                new FloatType((this.p1.x.value + this.p2.x.value) / 2.0f),
                new FloatType((this.p1.y.value + this.p2.y.value) / 2.0f)
        );
    }

    @Override
    public VarTypeEnum getType() {
        return this.type;
    }




    @Override
    public VarType getField(String fieldName) {
        switch (fieldName) {
            case "p1":
                return (this.p1);
            case "p2":
                return (this.p2);
            default:
                throw new RuntimeException("LINE has no field: " + fieldName);
        }
    }

    @Override
    public VarType setField(String fieldName, VarType value) {
        switch (fieldName) {
            case "p1": return this.p1 = (PointType) value;
            case "p2": return this.p2 = (PointType) value;
            default: throw new RuntimeException("LINE has no field: " + fieldName);
        }
    }

    @Override
    public VarType getMethod(String methodName, VarType... args) {
        return switch (methodName) {
            case "render" -> {
                DrawCollector.add(new DrawableLine(
                        this.p1.x.value, this.p1.y.value,
                        this.p2.x.value, this.p2.y.value));
                yield this;
            }
            case "move" -> {
                if (args.length != 2) {
                    throw new RuntimeException("LINE.move expects 2 arguments");
                }
                if (!(args[0] instanceof FloatType dx) || !(args[1] instanceof FloatType dy)) {
                    throw new RuntimeException("LINE.move expects FLOAT, FLOAT arguments");
                }
                this.p1 = new PointType(
                        new FloatType(this.p1.x.value + dx.value),
                        new FloatType(this.p1.y.value + dy.value)
                );
                this.p2 = new PointType(
                        new FloatType(this.p2.x.value + dx.value),
                        new FloatType(this.p2.y.value + dy.value)
                );
                yield this;
            }
            case "rotate" -> {
                if (args.length != 1 && args.length != 2) {
                    throw new RuntimeException("LINE.rotate expects 1 or 2 arguments");
                }
                if (!(args[0] instanceof FloatType angle)) {
                    throw new RuntimeException("LINE.rotate expects FLOAT angle as first argument");
                }
                PointType pivot;
                if (args.length == 2) {
                    if (!(args[1] instanceof PointType point)) {
                        throw new RuntimeException("LINE.rotate expects POINT as second argument");
                    }
                    pivot = point;
                } else {
                    pivot = getCenter();
                }
                this.p1 = rotatePoint(this.p1, pivot, angle.value);
                this.p2 = rotatePoint(this.p2, pivot, angle.value);
                yield this;
            }
            default -> throw new RuntimeException("LINE has no method: " + methodName);
        };
    }

    @Override
    public String toString() {
        return "(" + p1 + ", " + p2 + ")";
    }
}
