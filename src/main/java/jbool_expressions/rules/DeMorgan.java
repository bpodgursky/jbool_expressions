package jbool_expressions.rules;

import jbool_expressions.And;
import jbool_expressions.Expression;
import jbool_expressions.Not;
import jbool_expressions.Or;

import java.util.ArrayList;
import java.util.List;

public class DeMorgan<K> extends Rule<Not<K>, K> {

  @Override
  public Expression<K> applyInternal(Not<K> not) {
      Expression<K> e = not.getE();

      if(e instanceof And){
        And<K> internal = (And<K>) e;
        List<Expression<K>> morganed = new ArrayList<Expression<K>>();
        for(Expression<K> expr: internal.expressions){
          morganed.add(Not.of(expr));
        }
        return Or.of(morganed);
      }

      if(e instanceof Or){
        Or<K> internal = (Or<K>) e;
        List<Expression<K>> morganed = new ArrayList<Expression<K>>();
        for(Expression<K> expr: internal.expressions){
          morganed.add(Not.of(expr));
        }
        return new And<K>(morganed);
      }
    return not;
  }

  @Override
  protected boolean isApply(Expression input) {
    return input instanceof Not;
  }
}