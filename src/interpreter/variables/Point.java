package interpreter.variables;

public class Point implements Var {

    private final VarTypeEnum type;
    public FloatType x, y;

    public Point(FloatType x, FloatType y)
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
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
