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
    public VarType getField(String fieldName) {
        switch (fieldName) {
            case "x":
                return (this.x);
            case "y":
                return (this.y);
            default:
                throw new RuntimeException("POINT has no field: " + fieldName);
        }
    }

    @Override
    public void setField(String fieldName, VarType value) {
        switch (fieldName) {
            case "x": this.x = (FloatType) value; break;
            case "y": this.y = (FloatType) value; break;
            default: throw new RuntimeException("POINT has no field: " + fieldName);
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
