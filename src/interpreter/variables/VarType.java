package interpreter.variables;

public interface VarType {
    VarTypeEnum getType();
    VarType getField(String fieldName);
    void setField(String fieldName, VarType value);
}
