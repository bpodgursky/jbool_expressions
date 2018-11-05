package com.bpodgursky.jbool_expressions.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.InternFunction;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public class UnboundedRuleSetCache<K> implements RuleSetCache<K> {

  private final Map<Expression<K>, Expression<K>> cache;

  private final InternFunction<K> interningFunction;


  public UnboundedRuleSetCache(InternFunction<K> interningFunction) {
    this.cache = new HashMap<>();
    this.interningFunction = interningFunction;
  }

  @Override
  public Expression<K> get(Expression<K> input) {
    return cache.get(input);
  }

  @Override
  public void put(Expression<K> input, Expression<K> output, ExprOptions<K> options) {

    ExprFactory<K> factory = options.getExprFactory();

    Expression<K> shrunkInput = input.map(interningFunction, factory);
    Expression<K> shrunkOutput = output.map(interningFunction, factory);

    cache.put(shrunkInput, shrunkOutput);
  }


}
