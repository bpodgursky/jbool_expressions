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


  public static <K> Expression<K> aggreessiveDNF(){

    //  to dnf (A & B & C) | (A & B & D) => (E & C ) | (E & D)

    //

    //  to cnf, opposite

    //  does matter for qmc


    return null;
  }

  /**
   * This method transforms an expression to DNF, but at a variable cardinality less than 9, switches to the QuineMcCluskey algorithm.
   *
   * The problem with using the switch globally is that QMC can be considerably slower than "naiive" simplification on really simple expressions.
   * For example, (A | B | C | D | E | F | G) takes 15ms using the standard toSop rules, but 120ms using QMC.  But QMC is dramatically faster
   * on some larger expressions with the same number of variables -- see https://github.com/bpodgursky/jbool_expressions/issues/29 for an example.
   *
   * So for now, I'm going to add a new method instead of potentially introducing a performance regression on the old method.  If there's a smarter
   * way to "guess" which method would be faster on a given expression, I'm happy to use it, but nothing comes to mind.
   *
   */
  public static <K> Expression<K> toDNFViaQMC(Expression<K> root) {
    Set<K> variables = new HashSet<>();
    root.collectK(variables, QMC_CARDINALITY_CUTOFF + 1);

    if (variables.size() <= QMC_CARDINALITY_CUTOFF) {
      return QuineMcCluskey.toDNF(root);
    } else {
      return toSop(root);
    }
  }

  public static <K> Expression<K> toSop(Expression<K> root) {
    return applySet(applySet(root, RulesHelper.demorganRules()), RulesHelper.toSopRules());
  }

  public static <K> Expression<K> toSop2(Expression<K> root) {

    List<Rule<?, K>> rules = RulesHelper.toSopRules();
//    rules.add(new QMC<>());

    return applySet(applySet(root, RulesHelper.demorganRules()), rules);
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
