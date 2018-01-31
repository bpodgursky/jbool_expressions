package com.bpodgursky.jbool_expressions;

import java.util.Comparator;
import java.util.List;

import static com.bpodgursky.jbool_expressions.Seeds.AND_SEED;

public class And<K> extends NExpression<K> {
  public static final String EXPR_TYPE = "and";

  public static <K> And<K> of(Expression<K>[] children, Comparator<Expression> comparator) {
    return new And<K>(children, comparator);
  }

  private And(Expression<K>[] children, Comparator<Expression> comparator) {
    super(children, AND_SEED, comparator);
  }

  @Override
  public NExpression<K> create(Expression<K>[] children, Comparator<Expression> comparator) {
    return of(children, comparator);
  }

  @Override
  protected String getDelimiter() {
    return " & ";
  }

  public static <K> And<K> of(Expression<K> child1, Expression<K> child2, Expression<K> child3) {
    return of(ExprUtil.<K>list(child1, child2, child3));
  }

  public static <K> And<K> of(Expression<K> child1, Expression<K> child2) {
    return of(ExprUtil.<K>list(child1, child2));
  }

  @SafeVarargs
  public static <K> And<K> of(Expression<K>... children) {
    return new And<>(children, HASH_COMPARATOR);
  }

  public static <K> And<K> of(Expression<K> child1) {
    return of(ExprUtil.<K>list(child1));
  }

  public static <K> And<K> of(List<? extends Expression<K>> children) {
    return new And<K>(children.toArray(new Expression[children.size()]), HASH_COMPARATOR);
  }

  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }
}
