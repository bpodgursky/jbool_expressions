package com.bpodgursky.jbool_expressions.parsers;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.Variable;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExprParser {

  public static Expression<String> parse(String expression){
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
      CommonTree ast = (CommonTree) ret.getTree();
      return parse(ast, mapper);
    } catch (RecognitionException e) {
      throw new IllegalStateException("Recognition exception is never thrown, only declared.");
    }
  }

  public static <T> Expression<T> parse(Tree tree, TokenMapper<T> mapper){
    if(tree.getType() == BooleanExprParser.AND){
      List<Expression<T>> children = new ArrayList<Expression<T>>();
      for(int i = 0; i < tree.getChildCount(); i++){
        Tree child = tree.getChild(i);
        Expression<T> parse = parse(child, mapper);
        if(child.getType() == BooleanExprParser.AND) {
          children.addAll(Arrays.asList(((And<T>) parse).expressions));
        }
        else {
          children.add(parse);
        }
      }

      return And.of(children);
    }else if(tree.getType() == BooleanExprParser.OR){
      List<Expression<T>> children = new ArrayList<Expression<T>>();
      for(int i = 0; i < tree.getChildCount(); i++){
        Tree child = tree.getChild(i);
        Expression<T> parse = parse(child, mapper);
        if(child.getType() == BooleanExprParser.OR) {
          children.addAll(Arrays.asList(((Or<T>) parse).expressions));
        }
        else {
          children.add(parse);
        }
      }
      return Or.of(children);
    }else if(tree.getType() == BooleanExprParser.NOT){
      return Not.of(parse(tree.getChild(0), mapper));
    }else if(tree.getType() == BooleanExprParser.NAME){
      return Variable.of(mapper.getVariable(tree.getText()));
    } else if(tree.getType() == BooleanExprParser.QUOTED_NAME){
      return Variable.of(mapper.getVariable(tree.getText()));
    } else if(tree.getType() == BooleanExprParser.TRUE){
      return Literal.getTrue();
    }else if(tree.getType() == BooleanExprParser.FALSE){
      return Literal.getFalse();
    }
    else if(tree.getType() == BooleanExprParser.LPAREN){
      return parse(tree.getChild(0), mapper);
    }
    else{
      throw new RuntimeException("Unrecognized! "+tree.getType()+" "+tree.getText());
    }
  }

}
