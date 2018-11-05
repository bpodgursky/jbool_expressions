package com.bpodgursky.jbool_expressions.util;

import java.util.HashMap;
import java.util.Map;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;

public interface ExprFactory<K> {

  public Expression<K> not(Expression<K> child);

  public Expression<K> and(Expression<K>[] children);

  public Expression<K> or(Expression<K>[] children);

  public class Default<K> implements ExprFactory<K> {

    @Override
    public Expression<K> not(Expression<K> child) {
      return (Not.of(child));
    }

    @Override
    public Expression<K> and(Expression<K>[] children) {
      return (And.of(children));
    }

    @Override
    public Expression<K> or(Expression<K>[] children) {
      return (Or.of(children));
    }
  }


  public class Interning<K> implements ExprFactory<K> {

    private Map<Expression<K>, Expression<K>> values;

    public Interning(Map<Expression<K>, Expression<K>> internMap){
      this.values = internMap;
    }

    private Expression<K> intern(Expression<K> expr){
      if(values.containsKey(expr)){
        return values.get(expr);
      }
      values.put(expr, expr);
      return expr;
    }

    @Override
    public Expression<K> not(Expression<K> child) {
      return intern(Not.of(child));
    }

    @Override
    public Expression<K> and(Expression<K>[] children) {
      return intern(And.of(children));
    }

    @Override
    public Expression<K> or(Expression<K>[] children) {
      return intern(Or.of(children));
    }
  }

}
