package jbool_expressions;

import jbool_expressions.rules.Rule;

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

  public static <K> Variable<K> of(K value){
    return new Variable<K>(value);
  }
}
