package interpreter.variables;


public class LineType implements VarType {
    private final VarTypeEnum type;

    public PointType p1, p2;

    public LineType(PointType p1, PointType p2) {
        this.type = VarTypeEnum.LINE;
        this.p1 = p1;
        this.p2 = p2;

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
        throw new RuntimeException("LINE has no method: " + methodName);
    }

    @Override
    public String toString() {
        return "(" + p1 + ", " + p2 + ")";
    }
}
