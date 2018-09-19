package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;

public interface RuleSetCache<K> {
  public Expression<K> get(Expression<K> input);
  public void put(Expression<K> input, Expression<K> output);
}

