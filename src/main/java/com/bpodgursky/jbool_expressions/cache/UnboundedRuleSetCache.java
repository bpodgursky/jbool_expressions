package com.bpodgursky.jbool_expressions.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.InternFunction;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public class UnboundedRuleSetCache<K> implements RuleSetCache<K> {

  private final Map<String, Map<Expression<K>, Expression<K>>> cacheByRuleSet;

  private final InternFunction<K> interningFunction;


  public UnboundedRuleSetCache(InternFunction<K> interningFunction) {
    this.cacheByRuleSet = new HashMap<>();
    this.interningFunction = interningFunction;
  }

  @Override
  public Expression<K> get(RuleList<K> rules, Expression<K> input) {
    Map<Expression<K>, Expression<K>> cache = cacheByRuleSet.get(rules.getKey());

    if(cache == null){
      return null;
    }

    return cache.get(input);
  }

  @Override
  public void put(RuleList<K> rules, Expression<K> input, Expression<K> output, ExprOptions<K> options) {

    String ruleKey = rules.getKey();

    if(!cacheByRuleSet.containsKey(ruleKey)){
      cacheByRuleSet.put(ruleKey, new HashMap<>());
    }

    Map<Expression<K>, Expression<K>> cache = cacheByRuleSet.get(ruleKey);

    ExprFactory<K> factory = options.getExprFactory();

    Expression<K> shrunkInput = input.map(interningFunction, factory);
    Expression<K> shrunkOutput = output.map(interningFunction, factory);

    cache.put(shrunkInput, shrunkOutput);
  }


}
