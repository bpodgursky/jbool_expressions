package com.bpodgursky.jbool_expressions;

import java.util.List;
import java.util.Objects;

import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.google.common.base.Optional;

public class Not<K> extends Expression<K> {
  public static final String EXPR_TYPE = "not";
  private Optional<String> cachedStringRepresentation = Optional.absent();

  private final Expression<K> e;

  private Not(Expression<K> e) {
    this.e = e;
  }

  public Expression<K> getE() {
    return e;
  }

  public String toString() {
    if (!cachedStringRepresentation.isPresent()) {
      cachedStringRepresentation = Optional.of("!" + e);
    }
    return cachedStringRepresentation.get();
  }

  @Override
  public Expression<K> apply(List<Rule<?, K>> rules) {
    return new Not<K>(RuleSet.applyAll(e, rules));
  }

  public static <K> Not<K> of(Expression<K> e) {
    return new Not<K>(e);
  }

  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Not<?> not = (Not<?>) o;
    return Objects.equals(e, not.e);
  }

  @Override
  public int hashCode() {
    return Objects.hash(e);
  }
}
