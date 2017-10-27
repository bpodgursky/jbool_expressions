package com.bpodgursky.jbool_expressions.eval;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;

import java.util.Map;

public class EvalAnd<K> extends EvalRule<K> {
  @Override
  public boolean evaluate(Expression<K> expression, Map<String, EvalRule<K>> rules) {
    And<K> and = (And<K>) expression;

    for(Expression<K> e: and.expressions){

      if(!evaluateInternal(e, rules)){
        return false;
      }

    }
    return true;
  }
}
