package com.bpodgursky.jbool_expressions.rules;


import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

import java.util.ArrayList;
import java.util.List;

public class CombineAnd<K> extends Rule<And<K>, K> {

  @Override
  public Expression<K> applyInternal(And<K> and, ExprOptions<K> options) {
    for (Expression<K> expr : and.expressions) {
      if (expr instanceof And) {
        And<K> childAnd = (And<K>) expr;

        List<Expression<K>> newChildren = new ArrayList<>();
        ExprUtil.addAll(newChildren, ExprUtil.allExceptMatch(and.expressions, childAnd, options));
        ExprUtil.addAll(newChildren, childAnd.expressions);

        return options.getExprFactory().and(newChildren.toArray(new Expression[]{}));
      }
    }
    return and;
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof And;
  }
}

