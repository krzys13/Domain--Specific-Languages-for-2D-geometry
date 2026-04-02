parser grammar GeoLangParser;
options { tokenVocab=GeoLangLexer; }

program
    : stat* EOF
    ;

stat
    : decl ';' #decl_stat
    | expr ';' #expr_stat
    | '->' (expr | geo_value) ';' #print_stat
    ;

decl
    : type ID ('=' expr)?
    ;


expr
    : field                           #field_expr
    | assign                          #assign_expr
    | ID                              #id_expr
    | value                           #value_expr
    | l=expr op=(MUL|DIV) r=expr      #math_expr
    | l=expr op=(ADD|SUB) r=expr      #math_expr
    | '(' expr ')'                    #paren_expr
    | method                          #method_expr
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
    ID (DOT ID)* '(' (expr (',' expr)*)? ')'
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
