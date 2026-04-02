package interpreter.variables;

public interface VarType {
    VarTypeEnum getType();
    VarType getField(String fieldName);
    VarType setField(String fieldName, VarType value);
    VarType getMethod(String methodName, VarType... args);
}
