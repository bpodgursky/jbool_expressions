package com.bpodgursky.jbool_expressions.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.Variable;

public class ExprParser {

  public static Expression<String> parse(String expression) {
    if (expression == null) {
      return null;
    }
    return parse(expression, new IdentityMap());
  }

  public static <T> Expression<T> parse(String expression, TokenMapper<T> mapper) {
    try {
      //lexer splits input into tokens
      ANTLRStringStream input = new ANTLRStringStream(expression);
      TokenStream tokens = new CommonTokenStream(new BooleanExprLexer(input));

      //parser generates abstract syntax tree
      BooleanExprParser parser = new BooleanExprParser(tokens);
      BooleanExprParser.expression_return ret = parser.expression();

      //acquire parse result
      CommonTree ast = (CommonTree)ret.getTree();
      return parse(ast, mapper);
    } catch (RecognitionException e) {
      String exceptionMessage = e.getMessage();
      if (exceptionMessage == null || exceptionMessage.length() == 0) {
        if (e instanceof NoViableAltException) {
          exceptionMessage = "no viable alternative";
        } else {
          exceptionMessage = e.getClass().getName();
        }
      }
      String message = "Error at line " + e.line + ":" + e.charPositionInLine + " " + exceptionMessage + (e.token != null && e.token.getText() != null ? " at input " + e.token.getText() : "");
      throw new IllegalStateException(message, e);
    }
  }

  public static <T> Expression<T> parse(Tree tree, TokenMapper<T> mapper) {
    if (tree.getType() == BooleanExprParser.AND) {
      List<Expression<T>> children = new ArrayList<>();
      for (int i = 0; i < tree.getChildCount(); i++) {
        Tree child = tree.getChild(i);
        Expression<T> parse = parse(child, mapper);
        if (child.getType() == BooleanExprParser.AND) {
          children.addAll(Arrays.asList(((And<T>)parse).expressions));
        } else {
          children.add(parse);
        }
      }

      return And.of(children, null);
    } else if (tree.getType() == BooleanExprParser.OR) {
      List<Expression<T>> children = new ArrayList<>();
      for (int i = 0; i < tree.getChildCount(); i++) {
        Tree child = tree.getChild(i);
        Expression<T> parse = parse(child, mapper);
        if (child.getType() == BooleanExprParser.OR) {
          children.addAll(Arrays.asList(((Or<T>)parse).expressions));
        } else {
          children.add(parse);
        }
      }
      return Or.of(children, null);
    } else if (tree.getType() == BooleanExprParser.NOT) {
      return Not.of(parse(tree.getChild(0), mapper));
    } else if (tree.getType() == BooleanExprParser.VARIABLE) {
      String text = tree.getText();
      return Variable.of(text, mapper.getVariable(text));
    } else if (tree.getType() == BooleanExprParser.QUOTED_VARIABLE) {
      String text = tree.getText();
      return Variable.of(text, mapper.getVariable(text));
    } else if (tree.getType() == BooleanExprParser.DOUBLE_QUOTED_VARIABLE) {
      String text = tree.getText();
      return Variable.of(text, mapper.getVariable(text));
    } else if (tree.getType() == BooleanExprParser.BACKTICK_VARIABLE) {
      String text = tree.getText();
      return Variable.of(text, mapper.getVariable(text));
    } else if (tree.getType() == BooleanExprParser.TRUE) {
      return Literal.getTrue();
    } else if (tree.getType() == BooleanExprParser.FALSE) {
      return Literal.getFalse();
    } else if (tree.getType() == BooleanExprParser.LPAREN) {
      return parse(tree.getChild(0), mapper);
    } else if (tree.isNil()) {
      if (tree.getChildCount() > 0) {
        return parse(tree.getChild(0), mapper);
      } else {
        return null;
      }
    } else if (tree.getType() == BooleanExprParser.EOF) {
      return null;
    } else {
      throw new RuntimeException("Unrecognized! " + tree.getType() + " " + tree.getText());
    }
  }

  public static String escapeVariableName(String s) {
    if (s == null) {
      return null;
    }
    
    s = unescapeUnicode(s);
    
    s = s.replace("\\b", "\b");
    s = s.replace("\\t", "\t");
    s = s.replace("\\n", "\n");
    s = s.replace("\\f", "\f");
    s = s.replace("\\r", "\r");
    s = s.replace("\\\"", "\"");
    s = s.replace("\\'", "'");
    s = s.replace("\\`", "`");
    
    return s;
  }

  public static boolean isValidVariableName(String s) {
    if (s == null || s.length() == 0) {
      return false;
    }
    
    final int length = s.length();
    for (int offset = 0; offset < length;) {
      final int codepoint = s.codePointAt(offset);

      if (!(
            ((codepoint >= 'a' && codepoint <= 'z') || (codepoint >= 'A' && codepoint <= 'Z') || codepoint == '$' || codepoint == '_')  // ('a'..'z' | 'A'..'Z' | '$' | '_') // these are the "java letters" below 0x7F 
            ||
            ! ((codepoint >= '\uu0000' && codepoint <= '\u007F') || (codepoint >= '\uD800' && codepoint <= '\uDBFF')) // ~ ( '\\u0000' .. '\\u007F' | '\\uD800' .. '\\uDBFF' )  // covers all characters above 0x7F which are not a surrogate
            ||
            ((codepoint >= 0x10000 && codepoint <= 0x10FFFF)) //('\uD800' .. '\uDBFF') ('\uDC00' .. '\uDFFF') // covers UTF-16 surrogate pairs encodings for U+10000 (HEX) to U+10FFFF (HEX)
         )) {
        //not a letter
        if (offset == 0 || !(codepoint >= '0' && codepoint <= '9')) {
          //first character or not a digit
          return false;
        }
      }

      offset += Character.charCount(codepoint);
    }
    return true;
  }

  public static String unescapeVariableName(String s) {
    if (s == null) {
      return null;
    }
    
    s = unescapeUnicode(s);
    
    s = s.replace("\\b", "\b");
    s = s.replace("\\t", "\t");
    s = s.replace("\\n", "\n");
    s = s.replace("\\f", "\f");
    s = s.replace("\\r", "\r");
    s = s.replace("\\\"", "\"");
    s = s.replace("\\'", "'");
    s = s.replace("\\`", "`");
    
    return s;
  }
  
  public static String unescapeUnicode(String s) {
    StringBuilder sb = new StringBuilder();
  
    int oldIndex = 0;
  
    for (int i = 0; i + 2 < s.length(); i++) {
      if (s.substring(i, i + 2).equals("\\u")) {
        sb.append(s.substring(oldIndex, i));
        int codePoint = Integer.parseInt(s.substring(i + 2, i + 6), 16);
        sb.append(Character.toChars(codePoint));
  
        i += 5;
        oldIndex = i + 1;
      }
    }
  
    sb.append(s.substring(oldIndex, s.length()));
  
    return sb.toString();
  }
}
