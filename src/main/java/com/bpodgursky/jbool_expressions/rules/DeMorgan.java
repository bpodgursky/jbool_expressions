package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.ArrayList;
import java.util.List;

public class DeMorgan<K> extends Rule<Not<K>, K> {

  @Override
  public Expression<K> applyInternal(Not<K> not, ExprOptions<K> options) {
      Expression<K> e = not.getE();

    ExprFactory<K> factory = options.getExprFactory();

    if(e instanceof And){
        And<K> internal = (And<K>) e;
        List<Expression<K>> morganed = new ArrayList<>();
        for(Expression<K> expr: internal.expressions){
          morganed.add(factory.not(expr));
        }
        return factory.or(morganed.toArray(new Expression[morganed.size()]));
      }

      if(e instanceof Or){
        Or<K> internal = (Or<K>) e;
        List<Expression<K>> morganed = new ArrayList<>();
        for(Expression<K> expr: internal.expressions){
          morganed.add(factory.not(expr));
        }
        return factory.and(morganed.toArray(new Expression[morganed.size()]));
      }
    return not;
  }

  @Override
  protected boolean isApply(Expression input) {
    return input instanceof Not;
  }
}