package com.bpodgursky.jbool_expressions.rules;

import java.util.Map;
import java.util.function.Function;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.InternFunction;

public class Intern<K> implements InternFunction<K> {

  private final Map<Expression<K>, Expression<K>> cache;

  public Intern(Map<Expression<K>, Expression<K>> cache) {
    this.cache = cache;
  }

  @Override
  public Expression<K> apply(Expression<K> kExpression) {

    if (cache.containsKey(kExpression)) {
      return cache.get(kExpression);
    }

    cache.put(kExpression, kExpression);

    return kExpression;
  }
}
