lexer grammar GeoLangLexer;


POINT_TYPE : 'point' ;
CIRCLE_TYPE : 'circle' ;
LINE_TYPE : 'line' ;
FLOAT_TYPE : 'float';

FLOAT_VALUE : [0-9]+ ('.' [0-9]+)*;
ID : [a-zA-Z_][a-zA-Z0-9_]* ;

DIV : '/' ;

MUL : '*' ;

SUB : '-' ;

ADD : '+' ;

EQ : '=' ;
COMMA : ',' ;
SEMI : ';' ;
LPAREN : '(' ;
RPAREN : ')' ;
LCURLY : '{' ;
RCURLY : '}' ;
DOT : '.' ;
PRINT : '->';

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
LINE_COMMENT : '//' ~'\n'* '\n' -> channel(HIDDEN) ;
NEWLINE : [\r\n]+ -> channel(HIDDEN);
WS : [ \t]+ -> channel(HIDDEN);

