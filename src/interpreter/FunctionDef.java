package interpreter;

import grammar.GeoLangParser;
import interpreter.variables.VarTypeEnum;

import java.util.List;

public record FunctionDef(
        VarTypeEnum returnType,
        List<VarTypeEnum> paramTypes,
        List<String> paramNames,
        GeoLangParser.BlockContext body
) {
}
