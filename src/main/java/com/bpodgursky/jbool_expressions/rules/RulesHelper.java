package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.List;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

public class RulesHelper {

  public static <K> RuleList<K> simplifyRules() {
    List<Rule<?, K>> rules = new ArrayList<>();
    rules.add(new SimplifyAnd<K>());
    rules.add(new SimplifyOr<K>());
    rules.add(new SimplifyNot<K>());
    rules.add(new CombineAnd<K>());
    rules.add(new CombineOr<K>());
    rules.add(new SimplifyNExpression<K>());
    rules.add(new SimplifyNExprChildren<K>());
    rules.add(new CollapseNegation<K>());

    return new RuleList<K>(rules);
  }

  public static <K> RuleList<K> toSopRules() {
    List<Rule<?, K>> rules = new ArrayList<>(RulesHelper.<K>simplifyRules().getRules());
    rules.add(new ToSOP<K>());

    return new RuleList<>(rules);
  }

  public static <K> RuleList<K> demorganRules() {
    List<Rule<?, K>> rules = new ArrayList<>(RulesHelper.<K>simplifyRules().getRules());
    rules.add(new DeMorgan<K>());

    return new RuleList<>(rules);
  }

  public static <K> Expression<K> applyAll(Expression<K> e, RuleList<K> rules, ExprOptions<K> options) {

    Expression<K> cached = options.getRuleSetCache().get(rules, e);
    if (cached != null) {
      return cached;
    }

    Expression<K> orig = e;
    Expression<K> simplified = applyAllSingle(orig, rules, options);

    //  TODO pointer
    while (!orig.equals(simplified)) {
      orig = simplified;
      simplified = applyAllSingle(orig, rules, options);
    }

    options.getRuleSetCache().put(rules, e, simplified, options);

    return simplified;
  }

  private static <K> Expression<K> applyAllSingle(Expression<K> e, RuleList<K> rules, ExprOptions<K> options) {

    Expression<K> cachedSet = options.getRuleSetCache().get(rules, e);

    if (cachedSet != null) {
      return cachedSet;
    }

    Expression<K> tmp = e.apply(rules, options);

    for (Rule<?, K> r : rules.getRules()) {
      Expression<K> input = tmp;

      Expression<K> cachedRule = options.getRuleCache().get(r.getClass(), input);

      if (cachedRule != null) {
        tmp = cachedRule;
      } else {

        Expression<K> old = tmp;
        tmp = r.apply(tmp, options);

        if (!old.equals(tmp)) {
          options.getRuleCache().put(r.getClass(), input, tmp, options);
        }

      }

    }

    return tmp;
  }

  public static <K> Expression<K> applySet(Expression<K> root, RuleList<K> allRules, ExprOptions<K> options) {
    return applyAll(root, allRules, options);
  }


}
