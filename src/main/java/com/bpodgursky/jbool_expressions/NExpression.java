package com.bpodgursky.jbool_expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.rules.RulesHelper;

public abstract class NExpression<K> extends Expression<K> {

  public final Expression<K>[] expressions;
  private int hashCode;

  /**
   * @param expressions The expressions
   * @param seed        Each subclass of NExpression should have a different seed for hash code.
   *                    It allows better hash code generation.
   */
  protected NExpression(Expression<K>[] expressions, int seed, Comparator<Expression> sort) {
    if (expressions.length == 0) {
      throw new IllegalArgumentException("Arguments length 0!");
    }

    this.expressions = Arrays.copyOf(expressions, expressions.length);
    Arrays.sort(this.expressions, sort);

    //For NExpressions we compute the hash code up front and cache it.
    hashCode = Objects.hash(seed, Arrays.hashCode(this.expressions));
  }

  @Override
  public Expression<K> apply(List<Rule<?, K>> rules) {
    Expression<K>[] children = new Expression[this.expressions.length];
    for (int i = 0; i < this.expressions.length; i++) {
      children[i] = RulesHelper.applyAll(this.expressions[i], rules);
    }
    return create(children);
  }

  public List<Expression<K>> getChildren() {
    return ExprUtil.list(expressions);
  }

  public NExpression<K> create(Expression<K>[] children) {
    return create(children, HASH_COMPARATOR);
  }

  protected abstract NExpression<K> create(Expression<K>[] children, Comparator<Expression> comparator);

  @Override
  public Expression<K> sort(Comparator<Expression> comparator) {

    Expression<K>[] children = new Expression[this.expressions.length];
    for (int i = 0; i < this.expressions.length; i++) {
      children[i] = expressions[i].sort(comparator);
    }

    return create(children, comparator);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NExpression<?> that = (NExpression<?>)o;
    return hashCode == that.hashCode &&
        Arrays.equals(expressions, that.expressions);
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  public void collectK(Set<K> set, int limit){

    if(set.size() >= limit){
      return;
    }

    for (Expression<K> expression : expressions) {
      expression.collectK(set, limit);
    }
  }

}
