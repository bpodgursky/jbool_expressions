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

@parser::members {
    @Override
	public void emitErrorMessage(String msg) {
		throw new RuntimeException(msg);
	}
}

@rulecatch {
    catch (RecognitionException e) {
        throw e;
    }
}

@lexer::members {
    @Override
	public void emitErrorMessage(String msg) {
		throw new RuntimeException(msg);
	}
}

LPAREN : '(';
RPAREN : ')';
AND : '&' | ('A'|'a')('N'|'n')('D'|'d');
OR : '|' | ('O'|'o')('R'|'r');
NOT : '!' | ('N'|'n')('O'|'o')('T'|'t');
TRUE : ('T'|'t')('R'|'r')('U'|'u')('E'|'e') | '1';
FALSE : ('F'|'f')('A'|'a')('L'|'l')('S'|'s')('E'|'e') | '0';
VARIABLE : Letter LetterOrDigit*;
QUOTED_VARIABLE
    :   '\'' 
        (   EscapeSequence
        |   ~( '\'' | '\\' | '\r' | '\n' )        
        )* 
        '\'' 
    ;
DOUBLE_QUOTED_VARIABLE
    :   '"' 
        (   EscapeSequence
        |   ~( '"' | '\\' | '\r' | '\n' )        
        )* 
        '"' 
    ;
BACKTICK_VARIABLE
    :   '`' 
        (   EscapeSequence
        |   ~( '`' | '\\' | '\r' | '\n' )        
        )* 
        '`' 
    ;
WS : ( ' ' | '\t' | '\r' | '\n' | '\u000C')+ { $channel = HIDDEN; };
COMMENT
         @init{
            boolean isJavaDoc = false;
        }
    :   '/*'
            {
                if((char)input.LA(1) == '*'){
                    isJavaDoc = true;
                }
            }
        (options {greedy=false;} : . )* 
        '*/'
            {
                if(isJavaDoc==true){
                    $channel=HIDDEN;
                }else{
                    skip();
                }
            }
    ;
LINE_COMMENT
    :   
    '//' ~('\n'|'\r')*  ('\r\n' | '\r' | '\n') 
            {
                skip();
            }
    |   '//' ~('\n'|'\r')*     // a line comment could appear at the end of the file without CR/LF
            {
                skip();
            }
    ;   
RUBY_LINE_COMMENT
    :   
    '#' ~('\n'|'\r')*  ('\r\n' | '\r' | '\n') 
            {
                skip();
            }
    |   '#' ~('\n'|'\r')*     // a line comment could appear at the end of the file without CR/LF
            {
                skip();
            }
    ;   

expression : orexpression* EOF;
orexpression : andexpression (OR^ andexpression)*;
andexpression : notexpression (AND^ notexpression)*;
notexpression : NOT^ notexpression | atom;
atom : TRUE | FALSE | VARIABLE | QUOTED_VARIABLE | DOUBLE_QUOTED_VARIABLE | BACKTICK_VARIABLE | LPAREN^ orexpression RPAREN!;

fragment EscapeSequence
    : '\\' ('b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '`' | '\\')
    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
    ;
fragment HexDigits
    : HexDigit ((HexDigit | '_')* HexDigit)?
    ;
fragment HexDigit
    : ('0' .. '9' | 'a' .. 'f' | 'A' .. 'F')
    ;
    
fragment LetterOrDigit
    : Letter
    | '0'..'9'
    ;
fragment Letter
    : ('a'..'z' | 'A'..'Z' | '$' | '_') // these are the "java letters" below 0x7F
    | ~('\u0000' .. '\u007F' | '\uD800' .. '\uDBFF') // covers all characters above 0x7F which are not a surrogate
    | ('\uD800' .. '\uDBFF') ('\uDC00' .. '\uDFFF') // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;    

