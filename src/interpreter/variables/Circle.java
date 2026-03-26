package interpreter.variables;

public class Circle implements Var {

    private final VarTypeEnum type;

    public Point s; // center of cirlce
    public float r; // radius

    public Circle(Point s, float r) {
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
