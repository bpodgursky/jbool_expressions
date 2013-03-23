package com.bpodgursky.jbool_expressions;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class And<K> extends NExpression<K> {

  private And(List<Expression<K>> children) {
    super(children);
  }

  @Override
  protected Expression<K> createInternal(List<Expression<K>> children) {
    return new And<K>(children);
  }

  public String toString(){
    return "("+ StringUtils.join(expressions, " & ")+")";
  }

  @Override
  public boolean evaluate(EvaluationContext<K> context) {
    boolean value = true;
    for(Expression<K> e: expressions){
      value &= e.evaluate(context);
    }
    return value;
  }

  public static <K> And<K> of(Expression<K> child1, Expression<K> child2, Expression<K> child3){
    return of(ExprUtil.<K>list(child1, child2, child3));
  }

  public static <K> And<K> of(Expression<K> child1, Expression<K> child2){
    return of(ExprUtil.<K>list(child1, child2));
  }

  public static <K> And<K> of(Expression<K> child1){
    return of(ExprUtil.<K>list(child1));
  }

  public static <K> And<K> of(List<Expression<K>> children){
    return new And<K>(children);
  }

  @Override
  public boolean equals(Expression expr) {
    if(!(expr instanceof And)){
      return false;
    }
    And other = (And) expr;

    if(other.expressions.length != expressions.length){
      return false;
    }

    for(int i = 0; i < expressions.length; i++){
      if(!expressions[i].equals(other.expressions[i])){
        return false;
      }
    }
    return true;
  }
}
