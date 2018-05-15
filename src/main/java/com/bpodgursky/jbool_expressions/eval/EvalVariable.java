package com.bpodgursky.jbool_expressions.eval;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Variable;

import java.util.Map;

public class EvalVariable<K> extends EvalRule<K> {

  private final Map<K, Boolean> values;
  public EvalVariable(Map<K, Boolean> valueMap){
    this.values = valueMap;
  }

  @Override
  public boolean evaluate(Expression<K> expression, Map<String, EvalRule<K>> rules) {
    Variable<K> var = (Variable<K>) expression;
    return values.getOrDefault(var.getValue(), false);
  }
}
