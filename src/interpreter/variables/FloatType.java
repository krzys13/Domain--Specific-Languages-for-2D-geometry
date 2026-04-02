package interpreter.variables;

public class FloatType implements VarType
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
    public VarType getField(String name) {
        return null;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}
