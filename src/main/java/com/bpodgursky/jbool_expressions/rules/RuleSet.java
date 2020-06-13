package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Variable;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.cache.UnboundedRuleSetCache;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import static com.bpodgursky.jbool_expressions.rules.RulesHelper.applyAll;
import static com.bpodgursky.jbool_expressions.rules.RulesHelper.applySet;

//  intended user facing methods
public class RuleSet {

  private static final int QMC_CARDINALITY_CUTOFF = 8;

  public static <K> Expression<K> simplify(Expression<K> root) {
    return simplify(root, ExprOptions.noCaching());
  }

  public static <K> Expression<K> simplify(Expression<K> root, ExprOptions<K> options) {
    return applySet(root, RulesHelper.simplifyRules(), options);
  }

  /**
   * This method transforms an expression to DNF, but at a variable cardinality less than 9, switches to the QuineMcCluskey algorithm.
   * <p>
   * The problem with using the switch globally is that QMC can be considerably slower than "naiive" simplification on really simple expressions.
   * For example, (A | B | C | D | E | F | G) takes 15ms using the standard toSop rules, but 120ms using QMC.  But QMC is dramatically faster
   * on some larger expressions with the same number of variables -- see https://github.com/bpodgursky/jbool_expressions/issues/29 for an example.
   * <p>
   * So for now, I'm going to add a new method instead of potentially introducing a performance regression on the old method.  If there's a smarter
   * way to "guess" which method would be faster on a given expression, I'm happy to use it, but nothing comes to mind.
   */
  public static <K> Expression<K> toDNFViaQMC(Expression<K> root, ExprOptions<K> options) {
    Set<K> variables = new HashSet<>();
    root.collectK(variables, QMC_CARDINALITY_CUTOFF + 1);

    int varCount = variables.size();

    if (varCount <= QMC_CARDINALITY_CUTOFF && varCount > 0) {
      return QuineMcCluskey.toDNF(root, options);
    } else {
      return toSop(root);
    }
  }

  public static <K> Expression<K> toSop(Expression<K> root) {
    return toSop(root, ExprOptions.noCaching());
  }

  public static <K> Expression<K> toSop(Expression<K> root, ExprOptions<K> options) {
    root = root.map(options.getPreInternFunction(), options.getExprFactory());
    return applySet(applySet(root, RulesHelper.demorganRules(), options), RulesHelper.toSopRules(), options);
  }

  public static <K> Expression<K> toPos(Expression<K> root) {
    return toPos(root, ExprOptions.noCaching());
  }

  public static <K> Expression<K> toPos(Expression<K> root, ExprOptions<K> options) {

    //   not + toDNF
    Not<K> inverse = Not.of(root);
    Expression<K> sopInv = toSop(inverse, options);

    //  not + demorgan
    Not<K> inverse2 = Not.of(sopInv);

    return (applySet(inverse2, RulesHelper.demorganRules(), options));
  }

  static class Assign<K> implements Function<Expression<K>, Expression<K>> {

    private final Map<K, Boolean> values;

    public Assign(Map<K, Boolean> values) {
      this.values = values;
    }

    @Override
    public Expression<K> apply(Expression<K> kExpression) {

      if (kExpression instanceof Variable) {
        Variable<K> kVar = (Variable<K>)kExpression;
        K value = kVar.getValue();

        if (values.containsKey(value)) {
          return Literal.of(values.get(value));
        }

      }

      return kExpression;
    }
  }

  public static <K> Expression<K> assign(Expression<K> root, Map<K, Boolean> values) {
    return assign(root, values, ExprOptions.noCaching());
  }

  public static <K> Expression<K> assign(Expression<K> root, Map<K, Boolean> values, ExprOptions<K> options) {
    root = root.map(new Assign<>(values), options.getExprFactory());
    return applyAll(root, RulesHelper.simplifyRules(), options);
  }

  /**
   * More formal name for sum-of-products
   */
  public static <K> Expression<K> toDNF(Expression<K> root, ExprOptions<K> options) {
    return toSop(root, options);
  }

  public static <K> Expression<K> toDNF(Expression<K> root) {
    return toSop(root);
  }

  /**
   * More formal name for product-of-sums
   */
  public static <K> Expression<K> toCNF(Expression<K> root, ExprOptions<K> options) {
    return toPos(root, options);
  }

  public static <K> Expression<K> toCNF(Expression<K> root) {
    return toPos(root, ExprOptions.noCaching());
  }

}
