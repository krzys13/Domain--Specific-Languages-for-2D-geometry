package interpreter.variables;

public class CircleType implements VarType {

    private final VarTypeEnum type;

    public PointType c;// center of cirlce
    public FloatType r; // radius

    public CircleType(PointType s, FloatType r) {
        this.type = VarTypeEnum.CIRCLE;
        this.c = s;
        this.r = r;
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

        throw new RuntimeException("CIRCLE has no method: " + methodName);
    }

    @Override
    public String toString() {
        return "(" + c + ", " + r + ")";
    }
}
