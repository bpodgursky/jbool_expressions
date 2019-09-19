package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.PrintOptions.BooleanLiteralOption;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Literal<K> extends Expression<K> {
  public static final String EXPR_TYPE = "literal";

  private final boolean value;

  private static final Literal TRUE = new Literal(true);
  private static final Literal FALSE = new Literal(false);

  @SuppressWarnings("unchecked")
  public static <V> Literal<V> getTrue() {
    return TRUE;
  }

  @SuppressWarnings("unchecked")
  public static <V> Literal<V> getFalse() {
    return FALSE;
  }

  public static <K> Literal<K> of(boolean value) {
    if (value) {
      return getTrue();
    } else {
      return getFalse();
    }
  }

  private Literal(boolean value) {
    this.value = value;
  }

  public String toString() {
    return toString(PrintOptions.withDefaults());
  }

  @Override
  public String toString(PrintOptions options) {
    BooleanLiteralOption booleanLiteralOption = options.getBooleanLiteralOption();
    if (booleanLiteralOption == BooleanLiteralOption.AS_BINARY) {
      return Boolean.valueOf(value) ? "1" : "0";
    } else if (booleanLiteralOption == BooleanLiteralOption.AS_ENGLISH_TEXT_LOWERCASE) {
      return Boolean.valueOf(value) ? "true" : "false";
    } else if (booleanLiteralOption == BooleanLiteralOption.AS_ENGLISH_TEXT_UPPERCASE) {
      return Boolean.valueOf(value) ? "TRUE" : "FALSE";
    } else if (booleanLiteralOption == BooleanLiteralOption.AS_ENGLISH_TEXT_CAPITALIZE) {
      return Boolean.valueOf(value) ? "True" : "False";
    } else {
      throw new UnsupportedOperationException("Unsupported BooleanLiteralOption: " + booleanLiteralOption);
    }
  }
  
  public boolean getValue() {
    return value;
  }

  @Override
  public Expression<K> apply(RuleList<K> rules, ExprOptions<K> options) {
    return this;
  }

  @Override
  public List<Expression<K>> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public Expression<K> map(Function<Expression<K>, Expression<K>> function, ExprFactory<K> factory) {
    return function.apply(this);
  }

  @Override
  public Expression<K> sort(Comparator<Expression> comparator) {
    return this;
  }

  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }

  @Override
  public boolean equals(Object o) {
    return this == o;
  }

  @Override
  public int hashCode() {
    return Boolean.hashCode(value);
  }

  @Override
  public void collectK(Set<K> set, int limit) {
    //  no op
  }

  public Expression<K> replaceVars(Map<K, Expression<K>> m, ExprFactory<K> factory) {
    return this;
  }
}
