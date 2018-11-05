package com.bpodgursky.jbool_expressions;

import java.util.function.Function;

public interface InternFunction<K> extends Function<Expression<K>, Expression<K>> {

  public static class None<K> implements InternFunction<K> {
    @Override
    public Expression<K> apply(Expression<K> kExpression) {
      return kExpression;
    }
  }
}
