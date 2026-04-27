package interpreter;

import interpreter.variables.VarType;

public class ReturnSignal extends RuntimeException {
    private final VarType value;

    public ReturnSignal(VarType value) {
        this.value = value;
    }

    public VarType value() {
        return value;
    }
}
