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
    public String toString() {
        return "(" + p1 + ", " + p2 + ")";
    }
}
