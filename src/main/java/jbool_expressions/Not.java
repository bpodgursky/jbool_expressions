package jbool_expressions;

import jbool_expressions.rules.Rule;
import jbool_expressions.rules.RuleSet;

import java.util.List;

public class Not<K> extends Expression<K> {

  private final Expression<K> e;

  private Not(Expression<K> e){
    this.e = e;
  }

  public Expression<K> getE(){
    return e;
  }

  public String toString(){
    return "(! "+e+")";
  }

  @Override
  public boolean evaluate(EvaluationContext<K> context) {
    return !e.evaluate(context);
  }

  @Override
  public Expression<K> apply(List<Rule<?, K>> rules) {
    return new Not<K>(RuleSet.applyAll(e, rules));
  }

  public static <K> Not<K> of(Expression<K> e){
    return new Not<K>(e);
  }
}
