package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.rules.Rule;

import java.util.List;

public class Variable<K> extends Expression<K> {

  private final K value;

  private Variable(K value){
    this.value = value;
  }

  public K getValue(){
    return value;
  }

  public String toString(){
    return value.toString();
  }

  @Override
  public boolean evaluate(EvaluationContext<K> context) {
    return context.get(this);
  }

  @Override
  public Expression<K> apply(List<Rule<?, K>> rules) {
    return this;
  }

  @Override
  public boolean equals(Expression expr) {
    return expr instanceof Variable && ((Variable)expr).getValue().equals(getValue());
  }

  public static <K> Variable<K> of(K value){
    return new Variable<K>(value);
  }
}
