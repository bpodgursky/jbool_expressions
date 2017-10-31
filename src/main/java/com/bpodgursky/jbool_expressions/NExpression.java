package com.bpodgursky.jbool_expressions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.google.common.collect.Lists;

public abstract class NExpression<K> extends Expression<K>{

  public final Expression<K>[] expressions;
  private int hashCode;

  /**
   * @param expressions The expressions
   * @param seed Each subclass of NExpression should have a different seed for hash code.
   *             It allows better hash code generation.
   */
  protected NExpression(List<? extends Expression<K>> expressions, int seed){
    if(expressions.isEmpty()){
      throw new IllegalArgumentException("Arguments length 0!");
    }

    this.expressions = expressions.toArray(ExprUtil.<K>expr(expressions.size()));
    Arrays.sort(this.expressions);

    //For NExpressions we compute the hash code up front and cache it.
    hashCode = Objects.hash(seed, Arrays.hashCode(this.expressions));
  }

  @Override
  public Expression<K> apply(List<Rule<?, K>> rules) {
    List<Expression<K>> childCopy = Lists.newArrayList();
    for(Expression<K> expr: expressions){
      childCopy.add(RuleSet.applyAll(expr, rules));
    }
    return create(childCopy);
  }

  public List<Expression<K>> getChildren(){
    return ExprUtil.list(expressions);
  }

  public abstract NExpression<K> create(List<? extends Expression<K>> children);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NExpression<?> that = (NExpression<?>) o;
    return hashCode == that.hashCode &&
            Arrays.equals(expressions, that.expressions);
  }

  @Override
  public int hashCode() {
    return hashCode;
  }
}
