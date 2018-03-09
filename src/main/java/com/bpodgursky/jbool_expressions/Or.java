package com.bpodgursky.jbool_expressions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


import java.util.Optional;
import java.util.stream.Collectors;

import static com.bpodgursky.jbool_expressions.Seeds.OR_SEED;

public class Or<K> extends NExpression<K> {
  public static final String EXPR_TYPE = "or";
  private Optional<String> cachedStringRepresentation = Optional.empty();

  public static <K> Or<K> of(Expression<K>[] children, Comparator<Expression> comparator) {
    return new Or<K>(children, comparator);
  }

  private Or(Expression<K>[] children, Comparator<Expression> comparator) {
    super(children, OR_SEED, comparator);
  }

  @Override
  public NExpression<K> create(Expression<K>[] children, Comparator<Expression> comparator) {
    return of(children, comparator);
  }

  public String toString() {
    if (!cachedStringRepresentation.isPresent()) {
      cachedStringRepresentation = Optional.of("(" + String.join(" | ", Arrays.stream(expressions).map(Object::toString).collect(Collectors.toList())) + ")");
    }
    return cachedStringRepresentation.get();
  }

  @SafeVarargs
  public static <K> Or<K> of(Expression<K>... children) {
    return new Or<>(children, HASH_COMPARATOR);
  }

  public static <K> Or<K> of(Expression<K> child1, Expression<K> child2, Expression<K> child3, Expression<K> child4) {
    return of(ExprUtil.<K>list(child1, child2, child3, child4));
  }

  public static <K> Or<K> of(Expression<K> child1, Expression<K> child2, Expression<K> child3) {
    return of(ExprUtil.<K>list(child1, child2, child3));
  }

  public static <K> Or<K> of(Expression<K> child1, Expression<K> child2) {
    return of(ExprUtil.<K>list(child1, child2));
  }

  public static <K> Or<K> of(Expression<K> child1) {
    return of(ExprUtil.<K>list(child1));
  }

  public static <K> Or<K> of(List<? extends Expression<K>> children) {
    return of(children.toArray(new Expression[children.size()]), HASH_COMPARATOR);
  }

  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }
}
