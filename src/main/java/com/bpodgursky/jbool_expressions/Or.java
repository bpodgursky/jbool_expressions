package com.bpodgursky.jbool_expressions;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Optional;

public class Or<K> extends NExpression<K> {
  public static final String EXPR_TYPE = "or";
  private Optional<String> cachedStringRepresentation = Optional.absent();

  private Or(List<? extends Expression<K>> children) {
    super(children);
  }

  @Override
  public NExpression<K> create(List<? extends Expression<K>> children) {
    return new Or<K>(children);
  }

  public String toString() {
    if (!cachedStringRepresentation.isPresent()) {
      cachedStringRepresentation = Optional.of("(" + StringUtils.join(expressions, " | ") + ")");
    }
    return cachedStringRepresentation.get();
  }

  @Override
  public boolean equals(Expression expr) {
    if (!(expr instanceof Or)) {
      return false;
    }
    Or other = (Or)expr;

    if (other.expressions.length != expressions.length) {
      return false;
    }

    for (int i = 0; i < expressions.length; i++) {
      if (!expressions[i].equals(other.expressions[i])) {
        return false;
      }
    }

    return true;
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

  public static <K> Or<K> of(List<? extends Expression<K>> children) {
    return new Or<K>(children);
  }

  public static <K> Or<K> of(Expression<K> child1) {
    return of(ExprUtil.<K>list(child1));
  }

  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }
}
