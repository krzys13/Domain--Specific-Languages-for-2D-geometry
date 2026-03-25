package SymbolTable;

public class IntBoolType {
    private Integer value;
    public IntBoolType(Integer value) {
        this.value = value;
    }
    public IntBoolType(Boolean value) {
        this.value = value ? 1 : 0;
    }
    public Integer getInt() {
        return value;
    }
    public Integer setInt(Integer value) {
        this.value = value;
        return value;
    }
    public Boolean getBool() {
        return value != 0;
    }
    public Boolean setBool(Boolean value) {
        this.value = value ? 1 : 0;
        return value;
    }

}
