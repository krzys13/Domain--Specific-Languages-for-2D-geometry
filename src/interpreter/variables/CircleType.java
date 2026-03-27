package interpreter.variables;

public class CircleType implements VarType {

    private final VarTypeEnum type;

    public PointType s; // center of cirlce
    public FloatType r; // radius

    public CircleType(PointType s, FloatType r) {
        this.type = VarTypeEnum.CIRCLE;
        this.s = s;
        this.r = r;
    }

    @Override
    public VarTypeEnum getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "(" + s + ", " + r + ")";
    }
}
