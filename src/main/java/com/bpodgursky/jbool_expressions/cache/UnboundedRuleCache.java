package com.bpodgursky.jbool_expressions.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public class UnboundedRuleCache<K> implements RuleCache<K> {

  private final Map<Class<? extends Rule>, Map<Expression<K>, Expression<K>>> simplificatonCache;
  private final Function<Expression<K>, Expression<K>> interningFunction;

  public UnboundedRuleCache(Function<Expression<K>, Expression<K>> interningFunction) {
    this.interningFunction = interningFunction;
    this.simplificatonCache = new HashMap<>();
  }

  @Override
  public Expression<K> get(Class<? extends Rule> rule, Expression<K> input) {

    Map<Expression<K>, Expression<K>> forRule = simplificatonCache.get(rule);

    if (forRule != null) {
      return forRule.get(input);
    }

    return null;

  }

  @Override
  public void put(Class<? extends Rule> rule, Expression<K> input, Expression<K> output, ExprOptions<K> options) {

    ExprFactory<K> factory = options.getExprFactory();

    Expression<K> shrunkInput = input.map(interningFunction, factory);
    Expression<K> shrunkOutput = output.map(interningFunction, factory);

    if(!simplificatonCache.containsKey(rule)){
      simplificatonCache.put(rule, new HashMap<>());
    }

    Map<Expression<K>, Expression<K>> ruleCache = simplificatonCache.get(rule);
    ruleCache.put(shrunkInput, shrunkOutput);

  }

}
