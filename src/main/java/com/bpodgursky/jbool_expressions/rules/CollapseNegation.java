package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.NExpression;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

//  A | (!A & C) | D => A | C | D
//  A & (!A | C) & D => A & C & D
public class CollapseNegation<K> extends Rule<NExpression<K>, K> {

  public Expression<K> applyInternal(NExpression<K> input, ExprOptions<K> options) {

    //  case 1:
    //  A | (!A & C) | D
    if (input instanceof Or) {
      Or<K> inOr = (Or<K>)input;

      for (Expression<K> expression : input.getChildren()) {

        //  (!A & C)
        if (expression instanceof And) {
          And<K> child = (And<K>)expression;

          //  for every other term [A, D]
          for (Expression<K> otherChild : input.getChildren()) {

              // for each of !A and C
              for (Expression<K> subChild : child.getChildren()) {

                //  if otherChild == !subChild
                //  A and !A
                if (areNegation(subChild, otherChild)) {
                  return ExprUtil.stripNegation(inOr, child, subChild, options);
                }
              }
            }
          }
      }
    }

    if (input instanceof And) {
      And<K> andIn = (And<K>)input;
      for (Expression<K> expression : input.getChildren()) {
        if (expression instanceof Or) {
          Or<K> child = (Or<K>)expression;
          for (Expression<K> otherChild : input.getChildren()) {
            for (Expression<K> subChild : child.getChildren()) {
              if (areNegation(subChild, otherChild)) {
                return ExprUtil.stripNegation(andIn, child, subChild, options);
              }
            }
          }
        }
      }
    }

    return input;
  }

  //  simple -- not simplifying for more complex potential equality
  private boolean areNegation(Expression<K> child1, Expression<K> child2) {

    if (child1 instanceof Not) {
      Not child1Not = (Not)child1;
      if (child1Not.getE().equals(child2)) {
        return true;
      }
    }

    if (child2 instanceof Not) {
      Not child2Not = (Not)child2;
      if (child2Not.getE().equals(child1)) {
        return true;
      }
    }

    return false;
  }


  protected boolean isApply(Expression<K> input) {
    return input instanceof NExpression;
  }

}
