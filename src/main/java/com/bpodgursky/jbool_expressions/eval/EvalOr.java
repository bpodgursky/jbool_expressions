package com.bpodgursky.jbool_expressions.eval;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Or;

import java.util.Map;

public class EvalOr<K> extends EvalRule<K> {
  @Override
  public boolean evaluate(Expression<K> expression, Map<String, EvalRule<K>> rules) {
    Or<K> or = (Or<K>) expression;

    for(Expression<K> e: or.expressions){
      if(evaluateInternal(e, rules)){
        return true;
      }
    }

    return false;
  }
}
