package com.bpodgursky.jbool_expressions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class ExprUtil {

  public static <K> Expression<K> collapseToSOP(And<K> and, Or<K> internalOr, Expression<K> omitFromOr) {
    Expression<K>[] childrenNew = ExprUtil.allExceptMatch(and.expressions, internalOr);
    List<Expression<K>> newChildren = Lists.newArrayList();
    //  for each child of the or,  we want it AND all other children of the and

    for (Expression<K> orChild : internalOr.expressions) {
      if(!orChild.equals(omitFromOr)) {
        List<Expression<K>> andOthers = Lists.newArrayList();
        ExprUtil.addAll(andOthers, childrenNew);
        andOthers.add(orChild);

        newChildren.add(And.of(andOthers));
      }
    }

    return Or.of(newChildren);
  }

  public static <K> Expression<K> stripNegation(And<K> and, Or<K> internalOr, Expression<K> omitFromOr){
    Expression<K>[] childrenNew = ExprUtil.allExceptMatch(and.expressions, internalOr);
    List<Expression<K>> newChildren = Lists.newArrayList(childrenNew);

    List<Expression<K>> orOthers = Lists.newArrayList();
    for (Expression<K> orChild : internalOr.expressions) {

      if(!orChild.equals(omitFromOr)) {
        orOthers.add(orChild);
      }

    }

    newChildren.add(Or.of(orOthers));

    return And.of(newChildren);
  }

  public static <K> Expression<K> stripNegation(Or<K> or, And<K> internalAnd, Expression<K> omitFromAnd){
    Expression<K>[] childrenNew = ExprUtil.allExceptMatch(or.expressions, internalAnd);
    List<Expression<K>> newChildren = Lists.newArrayList(childrenNew);

    List<Expression<K>> andOthers = Lists.newArrayList();
    for (Expression<K> andChild : internalAnd.expressions) {

      if(!andChild.equals(omitFromAnd)) {
        andOthers.add(andChild);
      }

    }

    newChildren.add(And.of(andOthers));

    return Or.of(newChildren);
  }

  public static <K> Expression<K>[] allExceptMatch(Collection<Expression<K>> exprs, Set<? extends Expression<K>> omit){
    Set<Expression<K>> andTerms = new LinkedHashSet<Expression<K>>();
    for(Expression<K> eachExpr: exprs){
      if(!omit.contains(eachExpr)){
        andTerms.add(eachExpr);
      }
    }

    return toArray(andTerms);
  }

  public static <K> Expression<K>[] allExceptMatch(List<Expression<K>> exprs, Expression<K> omit) {
    //noinspection unchecked
    return allExceptMatch(exprs.toArray(new Expression[exprs.size()]), omit);
  }

    public static <K> Expression<K>[] allExceptMatch(Expression<K>[] exprs, Expression<K> omit){
    Set<Expression<K>> andTerms = new LinkedHashSet<Expression<K>>();
    for(Expression<K> eachExpr: exprs){
      if(!eachExpr.equals(omit)){
        andTerms.add(eachExpr);
      }
    }

    return toArray(andTerms);
  }

  private static <K> Expression<K>[] toArray(Set<Expression<K>> andTerms) {
    int i = 0;
    Expression<K>[] result = expr(andTerms.size());
    for(Expression<K> expr: andTerms){
      result[i++] = expr;
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <K> Expression<K>[] expr(int length){
    return new Expression[length];
  }

  public static <K> void addAll(Collection<Expression<K>> set, Expression<K>[] values){
    Collections.addAll(set, values);
  }

  @SuppressWarnings("unchecked")
  public static <K> List<Expression<K>> list(Expression... exprs){
    return Arrays.<Expression<K>>asList(exprs);
  }

  public static <K> Set<K> getVariables(Expression<K> expr){
    if(expr instanceof Variable){
      return Collections.singleton(((Variable<K>) expr).getValue());
    }else if(expr instanceof Not){
      return getVariables(((Not<K>) expr).getE());
    }else if(expr instanceof NExpression){
      Set<K> vars = new LinkedHashSet<K>();
      for(Expression<K> child: ((NExpression<K>)expr).expressions){
        vars.addAll(getVariables(child));
      }
      return vars;
    }
    return Collections.emptySet();
  }
}
