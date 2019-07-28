package com.bpodgursky.jbool_expressions.cache;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public interface RuleSetCache<K> {
  public Expression<K> get(RuleList<K> rules, Expression<K> input);

  public void put(RuleList<K> rules, Expression<K> input, Expression<K> output, ExprOptions<K> options);

  public static class NoCache<K> implements  RuleSetCache<K> {

    @Override
    public Expression<K> get(RuleList<K> rules, Expression<K> input) {
      return null;
    }

    @Override
    public void put(RuleList<K> rules, Expression<K> input, Expression<K> output, ExprOptions<K> options) {
      //  no op
    }

  }

}

