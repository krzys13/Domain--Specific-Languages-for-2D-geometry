package interpreter.variables;

public class UninitializedVarType implements VarType {
    private final VarTypeEnum type;

    public UninitializedVarType(VarTypeEnum type) {
        this.type = type;
    }

    @Override
    public VarTypeEnum getType() {
        return type;
    }

    @Override
    public VarType getField(String fieldName) {
        throw new RuntimeException("Uninitialized variable has no accessible fields");
    }

    @Override
    public VarType setField(String fieldName, VarType value) {
        throw new RuntimeException("Cannot assign field on uninitialized variable");
    }

    @Override
    public VarType getMethod(String methodName, VarType... args) {
        throw new RuntimeException("Cannot call method on uninitialized variable");
    }

    @Override
    public String toString() {
        return "<uninitialized " + type + ">";
    }
}
