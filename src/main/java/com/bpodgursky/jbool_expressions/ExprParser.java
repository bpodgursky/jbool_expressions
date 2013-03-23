package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.parsers.BooleanExprLexer;
import com.bpodgursky.jbool_expressions.parsers.BooleanExprParser;
import com.google.common.collect.Lists;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import java.util.List;

public class ExprParser {

  public static Expression<String> parse(String expression) {
    try {
      //lexer splits input into tokens
      ANTLRStringStream input = new ANTLRStringStream(expression);
      TokenStream tokens = new CommonTokenStream(new BooleanExprLexer(input));

      //parser generates abstract syntax tree
      BooleanExprParser parser = new BooleanExprParser(tokens);
      BooleanExprParser.expression_return ret = parser.expression();

      //acquire parse result
      CommonTree ast = (CommonTree) ret.getTree();
      return parse(ast);
    } catch (RecognitionException e) {
      throw new IllegalStateException("Recognition exception is never thrown, only declared.");
    }
  }

  public static Expression<String> parse(Tree tree){
    if(tree.getType() == BooleanExprParser.AND){
      List<Expression<String>> children = Lists.newArrayList();
      for(int i = 0; i < tree.getChildCount(); i++){
        children.add(parse(tree.getChild(i)));
      }
      return And.of(children);
    }else if(tree.getType() == BooleanExprParser.OR){
      List<Expression<String>> children = Lists.newArrayList();
      for(int i = 0; i < tree.getChildCount(); i++){
        children.add(parse(tree.getChild(i)));
      }
      return Or.of(children);
    }else if(tree.getType() == BooleanExprParser.NOT){
      return Not.of(parse(tree.getChild(0)));
    }else if(tree.getType() == BooleanExprParser.NAME){
      return Variable.of(tree.getText());
    }else if(tree.getType() == BooleanExprParser.TRUE){
      return Literal.getTrue();
    }else if(tree.getType() == BooleanExprParser.FALSE){
      return Literal.getFalse();
    }else{
      throw new RuntimeException("Unrecognized! "+tree.getType()+" "+tree.getText());
    }
  }
}
