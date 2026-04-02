package interpreter.variables;

public interface VarType {
    VarTypeEnum getType();
    VarType getField(String name);

}
