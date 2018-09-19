package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;

public abstract class Rule<E extends Expression<K>, K> {

  public abstract Expression<K> applyInternal(E input, RuleSetCache<K> cache);

  public Expression<K> apply(Expression<K> input, RuleSetCache<K> cache){
    if(isApply(input)){
      return applyInternal((E) input, cache);
    }
    return input;
  }

  protected abstract boolean isApply(Expression<K> input);
}
