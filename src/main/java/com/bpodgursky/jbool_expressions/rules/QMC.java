package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;

public class QMC<K> extends Rule<Expression<K>, K>  {
  @Override
  public Expression<K> applyInternal(Expression<K> input, RuleSetCache<K> cache) {

    Expression<K> output = QuineMcCluskey.toDNF(input, cache);
    cache.put(QMC.class, output, output);
    //  TODO i'm not sure whether or not this can be more widely applicable, but we want to shortcut any attempt to recalculate this
    return output;
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input.getAllK().size() <= 5;
  }
}
