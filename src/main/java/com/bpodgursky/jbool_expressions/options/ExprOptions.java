package com.bpodgursky.jbool_expressions.options;

import java.util.HashMap;
import java.util.Map;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.InternFunction;
import com.bpodgursky.jbool_expressions.cache.RuleCache;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.cache.UnboundedRuleCache;
import com.bpodgursky.jbool_expressions.cache.UnboundedRuleSetCache;
import com.bpodgursky.jbool_expressions.rules.Intern;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public class ExprOptions<K> {

  public InternFunction<K> getPreInternFunction() {
    return preInternFunction;
  }

  private InternFunction<K> preInternFunction;
  private RuleSetCache<K> ruleSetCache;
  private RuleCache<K> ruleCache;
  private ExprFactory<K> exprFactory;

  public ExprOptions(InternFunction<K> preInternFunction,
                     RuleSetCache<K> ruleSetCache,
                     RuleCache<K> ruleCache,
                     ExprFactory<K> exprFactory) {

    this.preInternFunction = preInternFunction;
    this.ruleSetCache = ruleSetCache;
    this.ruleCache = ruleCache;
    this.exprFactory = exprFactory;
  }

  public ExprFactory<K> getExprFactory() {
    return exprFactory;
  }

  public RuleCache<K> getRuleCache() {
    return this.ruleCache;
  }

  public RuleSetCache<K> getRuleSetCache() {
    return this.ruleSetCache;
  }

  //  static helpers

  /**
   * No caching, no interning
   */
  public static <K> ExprOptions<K> noCaching() {
    return new ExprOptions<>(
        new InternFunction.None<>(),
        new RuleSetCache.NoCache<>(),
        new RuleCache.NoCache<>(),
        new ExprFactory.Default<>()
    );
  }

  /**
   * Cache all rule results, don't intern anything
   */
  public static <K> ExprOptions<K> onlyCaching() {
    return new ExprOptions<>(
        new InternFunction.None<>(),
        new UnboundedRuleSetCache<>(new InternFunction.None<>()),
        new UnboundedRuleCache<>(new InternFunction.None<>()),
        new ExprFactory.Default<>()
    );
  }

  /**
   * Cache all rule results, and also intern all expressions for maximum memory optimization
   */
  public static <K> ExprOptions<K> allCacheIntern() {

    Map<Expression<K>, Expression<K>> internMap = new HashMap<>();
    Intern<K> internFunction = new Intern<>(internMap);

    return new ExprOptions<>(
        internFunction,
        new UnboundedRuleSetCache<>(internFunction),
        new UnboundedRuleCache<>(internFunction),
        new ExprFactory.Interning<>(internMap)
    );
  }

}
