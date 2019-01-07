package com.bpodgursky.jbool_expressions.cache;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.Rule;

public interface RuleCache<K> {
  public Expression<K> get(Class<? extends Rule> rule, Expression<K> input);
  public void put(Class<? extends Rule> rule, Expression<K> input, Expression<K> output, ExprOptions<K> options);

  public static class NoCache<K> implements  RuleCache<K> {

    @Override
    public Expression<K> get(Class<? extends Rule> rule, Expression<K> input) {
      return null;
    }

    @Override
    public void put(Class<? extends Rule> rule, Expression<K> input, Expression<K> output, ExprOptions<K> options) {
      // no-op
    }
  }
}
