package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

import java.util.ArrayList;
import java.util.List;

public class CombineOr<K> extends Rule<Or<K>, K> {

  @Override
  public Expression<K> applyInternal(Or<K> or, ExprOptions<K> options) {
    for (Expression<K> expr : or.expressions) {
      if (expr instanceof Or) {
        Or<K> childAnd = (Or<K>) expr;

        List<Expression<K>> newChildren = new ArrayList<>();
        ExprUtil.addAll(newChildren, ExprUtil.allExceptMatch(or.expressions, childAnd, options));
        ExprUtil.addAll(newChildren, childAnd.expressions);

        return options.getExprFactory().or(newChildren.toArray(new Expression[newChildren.size()]));
      }
    }
    return or;
  }

  @Override
  protected boolean isApply(Expression input) {
    return input instanceof Or;
  }

}
