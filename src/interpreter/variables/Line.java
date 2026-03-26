package interpreter.variables;

public class Line implements Var {
    private final VarTypeEnum type;

    public Point p1, p2;

    public Line( Point p1, Point p2) {
        this.type = VarTypeEnum.LINE;
        this.p1 = p1;
        this.p2 = p2;

    }

    @Override
    public VarTypeEnum getType() {
        return this.type;
    }
}
