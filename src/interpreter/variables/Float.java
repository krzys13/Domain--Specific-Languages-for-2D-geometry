package interpreter.variables;

public class Float implements VarType
{
    private final VarTypeEnum type;
    public float value;

    public Float(float value) {
        this.type = VarTypeEnum.FLOAT;
        this.value = value;
    }

    @Override
    public VarTypeEnum getType() {
        return this.type;
    }
}
