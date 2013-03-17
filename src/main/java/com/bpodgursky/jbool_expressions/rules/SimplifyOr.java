package com.bpodgursky.jbool_expressions.rules;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.bpodgursky.jbool_expressions.*;

import java.util.*;

public class SimplifyOr<K> extends SimplifyNExpression<K> {

  @Override
  protected boolean isMatch(Expression e) {
    return e instanceof Or;
  }

  @Override
  protected Expression<K> simplifyInternal(Expression<K>[] retain) {
    Set<Expression> internal = Sets.newHashSet();
    Collections.addAll(internal, retain);

    List<Expression<K>> copy = Lists.newArrayList();
    for (Expression<K> expr : retain) {
      if (expr instanceof Literal) {
        Literal l = (Literal) expr;

        //  ignore anything that is "false"
        if (!l.getValue()) {
          continue;
        } else {
          return Literal.of(true);
        }
      }

      //  succeed immediately if require something or its opposite
      if (internal.contains(Not.of(expr))) {
        return Literal.of(true);
      }

      copy.add(expr);
    }

    if (copy.isEmpty()) {
      return Literal.of(false);
    }

    return Or.of(copy);
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof Or;
  }
}
