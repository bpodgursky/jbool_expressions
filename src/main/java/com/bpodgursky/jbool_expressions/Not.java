package com.bpodgursky.jbool_expressions;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.rules.RulesHelper;
import java.util.Optional;

public class Not<K> extends Expression<K> {
  public static final String EXPR_TYPE = "not";
  private Optional<String> cachedStringRepresentation = Optional.empty();

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
    return new Not<K>(RulesHelper.applyAll(e, rules));
  }

  @Override
  public Expression<K> sort(Comparator<Expression> comparator) {
    return Not.of(e.sort(comparator));
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
  public Set<K> getAllK(){
	  Set<K> set=new HashSet<K>();
	  set.addAll(e.getAllK());
	  return set;
  }public Expression<K> replaceVariablesWith(Map<K,Expression<K>> m){
	  return of(e.replaceVariablesWith(m));
  }
}
