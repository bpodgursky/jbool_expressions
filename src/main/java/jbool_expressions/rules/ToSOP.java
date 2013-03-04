package jbool_expressions.rules;

import jbool_expressions.And;
import jbool_expressions.ExprUtil;
import jbool_expressions.Expression;
import jbool_expressions.Or;

import java.util.ArrayList;
import java.util.List;

public class ToSOP<K> extends Rule<And<K>, K> {

  @Override
  public Expression<K> applyInternal(And<K> and) {
    //  if there are any children which are ORs,
    for (Expression<K> e : and.expressions) {
      if (e instanceof Or) {
        Or<K> or = (Or<K>) e;

        Expression<K>[] childrenNew = ExprUtil.allExceptMatch(and.expressions, or);
        List<Expression<K>> newChildren = new ArrayList<Expression<K>>();
        //  for each child of the or,  we want it AND all other children of the and

        for (Expression<K> orChild : or.expressions) {
          List<Expression<K>> andOthers = new ArrayList<Expression<K>>();
          ExprUtil.addAll(andOthers, childrenNew);
          andOthers.add(orChild);

          newChildren.add(And.of(andOthers));
        }

        return Or.of(newChildren);
      }
    }
    return and;
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof And;
  }
}