package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

public abstract class Rule<E extends Expression<K>, K> {

  public abstract Expression<K> applyInternal(E input, ExprOptions<K> options);

  public Expression<K> apply(Expression<K> input, ExprOptions<K> options){
    if(isApply(input)){
      return applyInternal((E) input, options);
    }
    return input;
  }

  protected abstract boolean isApply(Expression<K> input);
}
