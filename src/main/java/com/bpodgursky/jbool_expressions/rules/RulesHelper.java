package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;

import java.util.ArrayList;
import java.util.List;

public class RulesHelper {

  protected static <K> List<Rule<?, K>> simplifyRules() {
    List<Rule<?, K>> rules = new ArrayList<Rule<?, K>>();
    rules.add(new SimplifyAnd<K>());
    rules.add(new SimplifyOr<K>());
    rules.add(new SimplifyNot<K>());
    rules.add(new CombineAnd<K>());
    rules.add(new CombineOr<K>());
    rules.add(new SimplifyNExpression<K>());
    rules.add(new SimplifyNExprChildren<K>());
    rules.add(new CollapseNegation<K>());

    return rules;
  }

  protected static <K> List<Rule<?, K>> toSopRules(){
    List<Rule<?, K>> rules = new ArrayList<Rule<?, K>>(RulesHelper.<K>simplifyRules());
    rules.add(new ToSOP<K>());

    return rules;
  }

  protected static <K> List<Rule<?, K>> demorganRules() {
    List<Rule<?, K>> rules = new ArrayList<Rule<?, K>>(RulesHelper.<K>simplifyRules());
    rules.add(new DeMorgan<K>());

    return rules;
  }

  public static <K> Expression<K> applyAll(Expression<K> e, List<Rule<?, K>> rules) {
    Expression<K> orig = e;
    Expression<K> simplified = applyAllSingle(orig, rules);

    while(!orig.equals(simplified)){
      orig = simplified;
      simplified = applyAllSingle(orig, rules);
    }

    return simplified;
  }

  private static <K> Expression<K> applyAllSingle(Expression<K> e, List<Rule<?, K>> rules) {
    Expression<K> tmp = e.apply(rules);
    for(Rule<?, K> r: rules){
      tmp = r.apply(tmp);
    }
    return tmp;
  }

  public static <K> Expression<K> applySet(Expression<K> root, List<Rule<?, K>> allRules) {
    return applyAll(root, allRules);
  }


}
