parser grammar GeoLangParser;
options { tokenVocab=GeoLangLexer; }


program
    : stat* EOF
    ;

stat: ID '=' expr ';'
    | decl ';'
    ;

decl: POINT ID '=' point_value
    | CIRCLE ID '=' circle_value
    | LINE ID '=' line_value
    ;

expr: ID
    | FLOAT
    | l=expr op=(MUL|DIV) r=expr
    | l=expr op=(ADD|SUB) r=expr
    | '(' expr ')'
    ;

point_value: '(' expr ',' expr ')'
    | ID
 ;

circle_value : '(' point_value ',' expr')'
    | ID
  ;

line_value : '(' point_value ',' point_value ')'
    | ID
    ;


