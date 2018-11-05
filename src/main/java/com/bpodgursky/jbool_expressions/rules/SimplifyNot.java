package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

public class SimplifyNot<K> extends Rule<Not<K>, K> {

  @Override
  public Expression<K> applyInternal(Not<K> input, ExprOptions<K> options) {
    Expression<K> e = input.getE();

      if(e instanceof Literal){
        Literal l = (Literal) e;
        return Literal.of(!l.getValue());
      }

      if(e instanceof Not){
        Not<K> internal = (Not<K>) e;
        return internal.getE();
      }
    return input;
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof Not;
  }
}
