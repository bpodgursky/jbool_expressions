package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;

public interface RuleSetCache<K> {
  public Expression<K> get(Expression<K> input);
  public Expression<K> get(Class<? extends Rule> rule, Expression<K> input);

  //  TODO this is wrong if the rule has content aka assign
  public void put(Class<? extends Rule> rule, Expression<K> input, Expression<K> output);
  public void put(Expression<K> input, Expression<K> output);

}

