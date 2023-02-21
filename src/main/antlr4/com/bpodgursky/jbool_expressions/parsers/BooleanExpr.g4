grammar BooleanExpr;

options {
  language = Java;
}

parse
 : expression EOF
 ;

expression
 : expression op=AND expression #andExpression
 | expression op=OR expression  #orExpression
 | atom                         #atomExpression
 ;

atom
 : bool                         #boolExpression
 | IDENTIFIER                   #identifierExpression
 | QUOTED_IDENTIFIER            #quotedIdentifierExpression
 | DOUBLE_QUOTED_IDENTIFIER     #doubleQuotedIdentifierExpression
 | NOT atom                     #notExpression
 | LPAREN expression RPAREN     #parenExpression
 ;

bool
 : TRUE | FALSE
 ;

AND : '&';
OR : '|';
NOT : '!';
TRUE : 'true';
FALSE : 'false';
LPAREN : '(';
RPAREN : ')';
IDENTIFIER : ('A'..'Z' | 'a'..'z' | '_' | '0'..'9')+;
QUOTED_IDENTIFIER : '\''~('\r' | '\n' | '\'')+'\'';
DOUBLE_QUOTED_IDENTIFIER : '"'~('\r' | '\n' | '"')+'"';
WS : [ \r\t\u000C\n]+ -> skip;