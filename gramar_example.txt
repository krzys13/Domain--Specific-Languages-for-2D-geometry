grammar first;

prog:	stat* EOF ;

def : name= ID '(' par += ID (',' par+=ID)* ')' body = block;

stat: expr #expr_stat
    | IF_kw '(' cond=expr ')' then=block  ('else' else=block)? #if_stat
    | FOR_kw '(' init=expr ';' cond=expr ';' step=expr ')' body=block #for_stat
    | '->' expr #print_stat


    ;

block : stat #block_single
    | '{' block* '}' #block_real
    ;

expr:
        // OPERACJJE ARTMETYCZNE
      NOT expr #notOp

    |   l=expr op=(MUL|DIV) r=expr #binOp
    |	l=expr op=(ADD|SUB) r=expr #binOp
        // POROWNANIA
    |   l=expr op=(LESS_THAN | LESS_EQUAL_THAN | MORE_THAN | MORE_EQUAL_THAN) r=expr #relOp
        // ROWNOSC
    |   l=expr op=(EQUAL | NOT_EQUAL) r=expr #eqOp

        // LOGICZNE
    |   l=expr op=AND r=expr #andOp
    |   l=expr op=OR r=expr #orOp



    |   ID #id_tok
    |	INT #int_tok
    |	'(' expr ')' #pars
    | <assoc=right> ID '=' expr # assign

    ;

IF_kw : 'if' ;
FOR_kw : 'for';

DIV : '/' ;

MUL : '*' ;

SUB : '-' ;

ADD : '+' ;

//NEWLINE : [\r\n]+ -> skip;
NEWLINE : [\r\n]+ -> channel(HIDDEN);

//WS : [ \t]+ -> skip ;
WS : [ \t]+ -> channel(HIDDEN) ;

INT     : [0-9]+ ;

AND : '&&' ;

OR: '||' ;

EQUAL: '==' ;

NOT_EQUAL : '!=';

NOT : '!';

LESS_THAN : '<';

LESS_EQUAL_THAN :'<=';

MORE_THAN : '>';

MORE_EQUAL_THAN : '>=';


ID : [a-zA-Z_][a-zA-Z0-9_]* ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
LINE_COMMENT : '//' ~'\n'* '\n' -> channel(HIDDEN) ;