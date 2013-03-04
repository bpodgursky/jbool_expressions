package jbool_expressions.rules;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jbool_expressions.*;

import java.util.*;

public class SimplifyAnd<K> extends SimplifyNExpression<K> {

  @Override
  protected boolean isMatch(Expression e) {
    return e instanceof And;
  }

  @Override
  protected Expression<K> simplifyInternal(Expression<K>[] retain) {
    Set<Expression> internal = Sets.newHashSet();
    Collections.addAll(internal, retain);

    List<Expression<K>> copy = Lists.newArrayList();
    for (Expression<K> expr : retain) {
      if (expr instanceof Literal) {
        Literal l = (Literal) expr;

        //  ignore anything that is "true"
        if (l.getValue()) {
          continue;
        } else {
          return Literal.getFalse();
        }
      }

      //  fail immediately if require something and its opposite
      if (internal.contains(Not.of(expr))) {
        return Literal.getFalse();
      }

      copy.add(expr);
    }

    if (copy.isEmpty()) {
      return Literal.getTrue();
    }

    return And.of(copy);
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof And;
  }
}
