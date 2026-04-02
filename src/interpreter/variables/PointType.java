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
    public VarType getField(String name) {
        switch (name) {
            case "x":
                return (this.x);
            case "y":
                return (this.y);
            default:
                throw new RuntimeException("POINT has no field: " + name);
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
