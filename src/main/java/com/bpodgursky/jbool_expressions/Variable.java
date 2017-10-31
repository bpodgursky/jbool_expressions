package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.rules.Rule;

import java.util.List;
import java.util.Objects;

public class Variable<K> extends Expression<K> {
  public static final String EXPR_TYPE = "variable";

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
  public Expression<K> apply(List<Rule<?, K>> rules) {
    return this;
  }

  public static <K> Variable<K> of(K value){
    return new Variable<K>(value);
  }

  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Variable<?> variable = (Variable<?>) o;
    return Objects.equals(value, variable.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
