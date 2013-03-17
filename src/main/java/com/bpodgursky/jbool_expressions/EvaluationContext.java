package com.bpodgursky.jbool_expressions;

import java.util.Map;

public class EvaluationContext<K> {

  private Map<K, Boolean> values;

  public EvaluationContext(Map<K, Boolean> values){
    this.values = values;
  }

  public boolean get(Variable<K> variable){
    if(!values.containsKey(variable.getValue())){
      throw new IllegalArgumentException("Variable "+variable+" not set!");
    }

    return values.get(variable.getValue());
  }
}
