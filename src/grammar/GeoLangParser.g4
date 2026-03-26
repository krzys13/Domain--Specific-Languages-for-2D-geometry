parser grammar GeoLangParser;
options { tokenVocab=GeoLangLexer; }

program
    : stat* EOF
    ;

stat
    : decl ';'
    | assign ';'
    | expr ';'
    ;

//==========================
// DECLARATIONS
// can be wit assignmnet
//==========================

decl
    : geo_type = (POINT|CIRCLE|LINE) ID ('=' geo_value)? #geo_decl
    | FLOAT ID ('=' expr)*      #flaot_decl
    ;
//==========================
// ASSIGNMENT
//==========================
assign
    : (ID | field_access) '=' (expr| geo_value)
    ;


expr
    : field_access
    | ID
    | FLOAT
    | '(' expr ')'
    | l=expr op=(MUL|DIV) r=expr
    | l=expr op=(ADD|SUB) r=expr
    ;
//==========================
// FIELD ACCESS
//==========================
field_access
    : ID (DOT ID)+
    ;

//=========================
// GEOMETRIC VALUES
//=========================

geo_value
    : point_value
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