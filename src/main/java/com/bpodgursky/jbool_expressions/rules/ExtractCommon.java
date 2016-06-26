package com.bpodgursky.jbool_expressions.rules;

import java.util.List;
import java.util.Set;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.NExpression;
import com.bpodgursky.jbool_expressions.Or;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

//  (A & B) | (A & C) => A & (B | C)
//  (A | B | D) & (A | C | E) & F=> (A | ((B|D) & (C|E))) & F
public class ExtractCommon<K> extends Rule<NExpression<K>, K> {

  private boolean oppositeNExpr(NExpression<K> first, Expression<K> second) {
    if (first instanceof And) {
      return second instanceof Or;
    }
    if (first instanceof Or) {
      return second instanceof And;
    }
    //  wut
    throw new RuntimeException();
  }

  private NExpression<K> ofSame(NExpression<K> first, List<? extends Expression<K>> others) {
    return first.create(others);
  }

  private NExpression<K> ofOpposite(NExpression<K> first, List<? extends Expression<K>> others) {

    if (first instanceof And) {
      return Or.of(others);
    }

    if (first instanceof Or) {
      return And.of(others);
    }

    throw new RuntimeException();
  }

  public Expression<K> applyInternal(NExpression<K> input) {

    HashMultimap<Expression<K>, NExpression<K>> byParent = HashMultimap.create();

    //  for every internal expression which is an Or, check # unique instances of each internal expr
    for (Expression<K> expression : input.getChildren()) {

      if (oppositeNExpr(input, expression)) {
        NExpression<K> inner = (NExpression<K>)expression;

        for (Expression<K> expr : inner.getChildren()) {
          byParent.put(expr, inner);
        }

      }

    }

    //  TODO get one with most?
    for (Expression<K> expression : byParent.keySet()) {
      Set<NExpression<K>> common = byParent.get(expression);

      if (common.size() > 1) {

        List<NExpression<K>> remainder = Lists.newArrayList();

        for (NExpression<K> subExpr : common) {

          Expression<K>[] remaining = ExprUtil.allExceptMatch(
              subExpr.getChildren(),
              expression
          );

          remainder.add(subExpr.create(Lists.newArrayList(remaining)));
        }

        List<Expression<K>> objects = Lists.newArrayList(ExprUtil.allExceptMatch(input.getChildren(), common));
        objects.add(ofOpposite(input, Lists.newArrayList(expression, ofSame(input, remainder))));

        if (objects.size() > 1) {
          return ofSame(input, objects);
        }

        return objects.get(0);

      }

    }

    return input;
  }

  protected boolean isApply(Expression<K> input) {
    return input instanceof NExpression;
  }


}
