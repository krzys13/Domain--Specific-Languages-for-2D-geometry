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
    public VarType getField(String fieldName) {
        return null;
    }

    @Override
    public VarType setField(String fieldName, VarType value) {
        throw new RuntimeException("FLOAT has no fields");
    }

    @Override
    public VarType getMethod(String methodName, VarType... args) {
        throw new RuntimeException("FLOAT has no method: " + methodName);
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}
