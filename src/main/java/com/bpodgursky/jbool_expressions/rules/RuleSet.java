package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;

import static com.bpodgursky.jbool_expressions.rules.RulesHelper.applySet;

//  intended user facing methods
public class RuleSet {

  public static <K> Expression<K> simplify(Expression<K> root) {
    return applySet(root, RulesHelper.<K>simplifyRules());
  }

  public static <K> Expression<K> toSop(Expression<K> root) {
    root = applySet(root, RulesHelper.<K>demorganRules());
    return applySet(root, RulesHelper.<K>toSopRules());
  }

  public static <K> Expression<K> toPos(Expression<K> root) {

    //   not + simplify
    Not<K> inverse = Not.of(root);
    Expression<K> sopInv = toSop(inverse);

    //  not + demorgan
    Not<K> inverse2 = Not.of(sopInv);

    return (applySet(inverse2, RulesHelper.<K>demorganRules()));
  }

  public static <K> Expression<K> assign(Expression<K> root, Map<K, Boolean> values) {
    List<Rule<?, K>> rules = new ArrayList<>(RulesHelper.<K>simplifyRules());
    rules.add(new Assign<K>(values));
    return applySet(root, rules);
  }

  /**
   * More formal name for sum-of-products
   */
  public static <K> Expression<K> toDNF(Expression<K> root) {
    return toSop(root);
  }

  /**
   * More formal name for product-of-sums
   */
  public static <K> Expression<K> toCNF(Expression<K> root) {
    return toPos(root);
  }

}
