parser grammar GeoLangParser;
options { tokenVocab=GeoLangLexer; }

program
    : stat* EOF
    ;

stat
    : decl ';'
    | expr ';'
    ;

decl
    : (POINT|CIRCLE|LINE) ID '=' geo_type
    ;

expr
    : field_access
    |geo_type
    | ID
    | FLOAT
    | '(' expr ')'
    | l=expr op=(MUL|DIV) r=expr
    | l=expr op=(ADD|SUB) r=expr
    | (ID | field_access) '=' expr
    ;

field_access
    : ID (DOT ID)+
    ;


geo_type : point_value
    | circle_value
    | line_value
    ;

point_value
    : '(' expr ',' expr ')'
    | ID
    ;

circle_value
    : '(' point_value ',' expr ')'
    | ID
    ;

line_value
    : '(' point_value ',' point_value ')'
    | ID
    ;