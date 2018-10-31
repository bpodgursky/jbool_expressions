package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public interface RuleSetCache<K> {
  public Expression<K> get(Expression<K> input);
  public Expression<K> get(Class<? extends Rule> rule, Expression<K> input);

  public void put(Class<? extends Rule> rule, Expression<K> input, Expression<K> output);
  public void put(Expression<K> input, Expression<K> output);

  public ExprFactory<K> factory();

}

