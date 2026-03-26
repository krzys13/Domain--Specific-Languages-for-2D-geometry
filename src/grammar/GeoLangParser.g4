parser grammar GeoLangParser;
options { tokenVocab=GeoLangLexer; }

program
    : stat* EOF
    ;

stat
    : decl ';' #decl_stat
    | expr ';' #expr_stat
    | '->' ';' #print_stat
    ;

decl
    : type ID ('=' expr)?
    ;

type
    :FLOAT
    |POINT
    |CIRCLE
    |LINE
    ;

expr
    : field_access                    #field_access_expr
    | tuple                           #tuple_expr
    | assign                          #assign_expr
    | ID                              #id_expr
    | FLOAT                           #float_expr
    | l=expr op=(MUL|DIV) r=expr      #mul_div_expr
    | l=expr op=(ADD|SUB) r=expr      #add_sub_expr
    | '(' expr ')'                    #paren_expr
    ;

assign
    : (ID | field_access) '=' expr
    ;

tuple
    : '(' expr (',' expr)+ ')'
    ;

field_access
    : ID (DOT ID)+
    ;