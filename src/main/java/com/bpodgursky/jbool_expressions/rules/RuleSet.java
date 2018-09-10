package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;

import static com.bpodgursky.jbool_expressions.rules.RulesHelper.applySet;

//  intended user facing methods
public class RuleSet {

  private static final int QMC_CARDINALITY_CUTOFF = 8;

  public static <K> Expression<K> simplify(Expression<K> root) {
    return applySet(root, RulesHelper.simplifyRules());
  }

  public static <K> Expression<K> toSop(Expression<K> root) {

    Set<K> variables = new HashSet<>();
    root.collectK(variables, QMC_CARDINALITY_CUTOFF+1);

    if (variables.size() <= QMC_CARDINALITY_CUTOFF) {
      return QuineMcCluskey.toDNF(root);
    }
     else {
      return applySet(applySet(root, RulesHelper.demorganRules()), RulesHelper.toSopRules());
    }
  }

  public static <K> Expression<K> toPos(Expression<K> root) {

    //   not + toDNF
    Not<K> inverse = Not.of(root);
    Expression<K> sopInv = toSop(inverse);

    //  not + demorgan
    Not<K> inverse2 = Not.of(sopInv);

    return (applySet(inverse2, RulesHelper.demorganRules()));
  }

  public static <K> Expression<K> assign(Expression<K> root, Map<K, Boolean> values) {
    List<Rule<?, K>> rules = new ArrayList<>(RulesHelper.simplifyRules());
    rules.add(new Assign<>(values));
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
