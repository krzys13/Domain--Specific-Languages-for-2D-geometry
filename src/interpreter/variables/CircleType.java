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
    public VarType getField(String name) {
        switch (name) {
            case "c":
                return (this.c);
            case "r":
                return (this.r);
            default:
                throw new RuntimeException("CIRCLE has no field: " + name);
        }
    }

    @Override
    public String toString() {
        return "(" + c + ", " + r + ")";
    }
}
