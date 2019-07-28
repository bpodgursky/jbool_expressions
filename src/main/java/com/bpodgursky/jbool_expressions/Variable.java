package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class Variable<K> extends Expression<K> {
  public static final String EXPR_TYPE = "variable";

  private final K value;

  private Variable(K value) {
    this.value = value;
  }

  public K getValue() {
    return value;
  }

  public String toString() {
    return value.toString();
  }

  @Override
  public Expression<K> apply(RuleList<K> rules, ExprOptions<K> options) {
    return this;
  }

  @Override
  public List<Expression<K>> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public Expression<K> map(Function<Expression<K>, Expression<K>> function, ExprFactory<K> factory) {
    return function.apply(this);
  }

  @Override
  public Expression<K> sort(Comparator<Expression> comparator) {
    return this;
  }

  public static <K> Variable<K> of(K value) {
    return new Variable<K>(value);
  }

  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Variable<?> variable = (Variable<?>)o;
    return Objects.equals(value, variable.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public void collectK(Set<K> set, int limit) {

    if(set.size() >= limit){
      return;
    }

    set.add(value);
  }

  public Expression<K> replaceVars(Map<K, Expression<K>> m, ExprFactory<K> factory) {
    if (m.containsKey(getValue())) {
      return m.get(getValue());
    }
    return this;
  }
}
