package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Variable;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import static com.bpodgursky.jbool_expressions.rules.RulesHelper.applyAll;
import static com.bpodgursky.jbool_expressions.rules.RulesHelper.applySet;

//  intended user facing methods
public class RuleSet {

  private static final int QMC_CARDINALITY_CUTOFF = 8;

  public static <K> Expression<K> simplify(Expression<K> root) {
    return applySet(root, RulesHelper.simplifyRules());
  }


  public static <K> Expression<K> aggreessiveDNF() {

    //  to dnf (A & B & C) | (A & B & D) => (E & C ) | (E & D)

    //

    //  to cnf, opposite

    //  does matter for qmc


    return null;
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
  public static <K> Expression<K> toDNFViaQMC(Expression<K> root) {
    Set<K> variables = new HashSet<>();
    root.collectK(variables, QMC_CARDINALITY_CUTOFF + 1);

    if (variables.size() <= QMC_CARDINALITY_CUTOFF) {
      return QuineMcCluskey.toDNF(root, new RulesHelper.UnboundedCache<>(new ExprFactory.Default<>()));
    } else {
      return toSop(root);
    }
  }

  public static <K> Expression<K> toSop(Expression<K> root) {
    return applySet(applySet(root, RulesHelper.demorganRules()), RulesHelper.toSopRules());
  }

  public static <K> Expression<K> toSop2(Expression<K> root) {


    //  TODO all expression creation internally uses the interning factory

    //  TODO use pointer equality in rule checking
    //  TODO this is ^ risky because we have to use an unbounded cache for it to be correct


    List<Rule<?, K>> rules = new ArrayList<>(RulesHelper.simplifyRules());
//    rules.add(new QMC<>());
    rules.add(new ToSOP<>());

    Map<Expression<K>, Expression<K>> internMap = new HashMap<>();
    ExprFactory.Default<K> factory = new ExprFactory.Default<>();
    root = root.map(new Intern<>(internMap), factory);


    RulesHelper.UnboundedCache<K> cache = new RulesHelper.UnboundedCache<>(factory);

    Expression<K> morganed = applyAll(root, RulesHelper.demorganRules(), cache);

    morganed = morganed.map(new Intern<>(internMap), factory);

    return applyAll(morganed, rules, cache);

  }

  public static <K> Expression<K> toPos(Expression<K> root) {

    //   not + toDNF
    Not<K> inverse = Not.of(root);
    Expression<K> sopInv = toSop(inverse);

    //  not + demorgan
    Not<K> inverse2 = Not.of(sopInv);

    return (applySet(inverse2, RulesHelper.demorganRules()));
  }

  static class Intern<K> implements Function<Expression<K>, Expression<K>> {

    private final Map<Expression<K>, Expression<K>> cache;

    public Intern(Map<Expression<K>, Expression<K>> cache) {
      this.cache = cache;
    }

    @Override
    public Expression<K> apply(Expression<K> kExpression) {

      if (cache.containsKey(kExpression)) {
        return cache.get(kExpression);
      }

      cache.put(kExpression, kExpression);

      return kExpression;
    }
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

  //  TODO only for testing
  public static <K> Expression<K> assign(Expression<K> root, Map<K, Boolean> values) {
    return assign(root, values, new ExprFactory.Interning<>(new HashMap<>()));
  }

  public static <K> Expression<K> assign(Expression<K> root, Map<K, Boolean> values, RuleSetCache<K> cache, ExprFactory<K> factory) {
    root = root.map(new Assign<>(values), factory);
    return applyAll(root, RulesHelper.simplifyRules(), cache);
  }

  public static <K> Expression<K> assign(Expression<K> root, Map<K, Boolean> values, ExprFactory<K> factory) {
    root = root.map(new Assign<>(values), factory);
    return applySet(root, RulesHelper.simplifyRules());
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
