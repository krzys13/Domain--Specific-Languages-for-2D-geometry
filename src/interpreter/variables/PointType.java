package interpreter.variables;

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
            case "move" -> {
                this.x = new FloatType(7);
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
