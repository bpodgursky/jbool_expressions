package jbool_expressions.rules;

import jbool_expressions.Expression;
import jbool_expressions.Literal;
import jbool_expressions.Variable;

import java.util.Map;

public class Assign<K> extends Rule<Variable<K>, K> {
  private Map<K, Boolean> values;

  public Assign(Map<K, Boolean> values){
    this.values = values;
  }

  @Override
  public Expression<K> applyInternal(Variable<K> var) {
    if(values.containsKey(var.getValue())){
      return Literal.of(values.get(var.getValue()));
    }
    return var;
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof Variable;
  }
}
