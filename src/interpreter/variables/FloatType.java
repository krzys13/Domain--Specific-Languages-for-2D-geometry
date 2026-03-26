package interpreter.variables;

public class FloatType implements Var
{
    private final VarTypeEnum type;
    public float value;

    public FloatType(float value) {
        this.type = VarTypeEnum.FLOAT;
        this.value = value;
    }

    @Override
    public VarTypeEnum getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}
