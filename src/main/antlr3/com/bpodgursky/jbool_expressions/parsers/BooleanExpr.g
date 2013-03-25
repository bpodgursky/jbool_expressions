grammar BooleanExpr;

options
{
  language = Java;
  output = AST;
}

@lexer::header {
package com.bpodgursky.jbool_expressions.parsers;
}

@parser::header {
package com.bpodgursky.jbool_expressions.parsers;
}

LPAREN : '(' ;
RPAREN : ')' ;
AND : '&';
OR : '|';
NOT : '!';
NAME : ('A'..'Z')+;
QUOTED_NAME : '"'~('\r' | '\n' | '"')+ '"';
WS : ( ' ' | '\t' | '\r' | '\n' )+ { $channel = HIDDEN; };
TRUE : 'true';
FALSE : 'false';

expression : orexpression;
orexpression : andexpression (OR^ andexpression)*;
andexpression : notexpression (AND^ notexpression)*;
notexpression : NOT^ atom | atom;
atom : TRUE | FALSE | NAME | QUOTED_NAME | LPAREN! orexpression RPAREN!;
