parser grammar GeoLangParser;
options { tokenVocab=GeoLangLexer; }

program
    : stat* EOF
    ;

stat
    : decl ';' #decl_stat
    | expr ';' #expr_stat
    | '->' (expr | geo_value) ';' #print_stat
    | func_def #func_def_stat
    | return_stat ';' #return_stmt_stat
    ;

decl
    : type ID ('=' expr)?
    ;

func_def
    : FUNC type ID '(' param_list? ')' block
    ;

param_list
    : param (',' param)*
    ;

param
    : type ID
    ;

block
    : '{' stat* '}'
    ;

return_stat
    : RETURN expr
    ;

expr
    : field                           #field_expr
    | assign                          #assign_expr
    | method                          #method_expr
    | func_call                       #func_call_expr
    | ID                              #id_expr
    | value                           #value_expr
    | SUB expr                        #unary_minus_expr
    | l=expr op=(MUL|DIV) r=expr      #math_expr
    | l=expr op=(ADD|SUB) r=expr      #math_expr
    | '(' expr ')'                    #paren_expr
    ;

assign:
    (ID|field) '=' expr
    ;


type
    :FLOAT_TYPE
    |geo_type
    ;

geo_type
    :POINT_TYPE
    |CIRCLE_TYPE
    |LINE_TYPE
    ;

value
    :FLOAT_VALUE
    |geo_value
    ;

field
    : ID (DOT ID)+
    ;

method
    :
    ID (DOT ID)+ '(' (expr (',' expr)*)? ')'
    ;

func_call
    : ID '(' (expr (',' expr)*)? ')'
    ;

geo_value
    : line_value
    | circle_value
    | point_value
    ;

point_value
    : '(' l = expr ',' r = expr ')'
    ;

point_ref
    : point_value
    | ID
    ;

line_value
    : '(' l = point_ref ',' r = point_ref ')'
    ;
circle_value
    : '(' l = point_ref ',' r = expr ')'
    ;
