package interpreter.variables;

public class Point implements VarType {

    private final VarTypeEnum type;
    public float x, y;

    public Point(float x, float y)
    {
        this.type = VarTypeEnum.POINT
        this.x = x;
        this.y = y;

    }
    @Override
    public VarTypeEnum getType() {
        return this.type;
    }
}
