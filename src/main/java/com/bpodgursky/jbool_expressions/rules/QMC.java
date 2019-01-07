package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

public class QMC<K> extends Rule<Expression<K>, K>  {
  @Override
  public Expression<K> applyInternal(Expression<K> input, ExprOptions<K> options) {

    Expression<K> output = QuineMcCluskey.toDNF(input, options);
    options.getRuleCache().put(QMC.class, output, output, options);
    //  TODO i'm not sure whether or not this can be more widely applicable, but we want to shortcut any attempt to recalculate this
    //  since it is idempotent
    return output;
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input.getAllK().size() <= 5;
  }
}
