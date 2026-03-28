parser grammar GeoLangParser;
options { tokenVocab=GeoLangLexer; }

program
    : stat* EOF
    ;

stat
    : float_decl ';' #float_decl_stat
    |geo_decl ';' #geo_decl_stat
    |expr ';' #expr_stat
    |geo_assign #geo_assign_stat
    | '->' ';' #print_stat
    ;

float_decl
    : FLOAT_TYPE ID '=' expr
    ;
geo_decl
    : type = POINT_TYPE ID '=' point_value
    | type = LINE_TYPE ID '=' line_value
    | type = CIRCLE_TYPE ID '=' circle_value
    ;

geo_assign
    : (ID | field_access) '=' geo_value
    ;


expr
    : field_access                    #field_access_expr
    | float_assign                    #float_assign_expr
    | ID                              #id_expr
    | FLOAT                           #float_num_expr
    | l=expr op=(MUL|DIV) r=expr      #math_expr
    | l=expr op=(ADD|SUB) r=expr      #math_expr
    | '(' expr ')'                    #paren_expr
    ;

float_assign
    : (ID | field_access) '=' expr
    ;


field_access
    : ID (DOT ID)+
    ;

geo_value
    : point_value
    | line_value
    | circle_value
    ;

geo_type
    :POINT_TYPE
    |CIRCLE_TYPE
    |LINE_TYPE
    ;

point_value
    : '(' l = expr ',' r = expr ')'
    ;
line_value
    : '(' l = point_value ',' r = point_value ')'
    ;
circle_value
    : '(' l = point_value ',' r = expr ')'
    ;
