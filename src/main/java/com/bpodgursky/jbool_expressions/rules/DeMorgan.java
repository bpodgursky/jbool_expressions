package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;

import java.util.ArrayList;
import java.util.List;

public class DeMorgan<K> extends Rule<Not<K>, K> {

  @Override
  public Expression<K> applyInternal(Not<K> not, RuleSetCache<K> cache) {
      Expression<K> e = not.getE();

      if(e instanceof And){
        And<K> internal = (And<K>) e;
        List<Expression<K>> morganed = new ArrayList<>();
        for(Expression<K> expr: internal.expressions){
          morganed.add(Not.of(expr));
        }
        return Or.of(morganed);
      }

      if(e instanceof Or){
        Or<K> internal = (Or<K>) e;
        List<Expression<K>> morganed = new ArrayList<>();
        for(Expression<K> expr: internal.expressions){
          morganed.add(Not.of(expr));
        }
        return And.of(morganed);
      }
    return not;
  }

  @Override
  protected boolean isApply(Expression input) {
    return input instanceof Not;
  }
}