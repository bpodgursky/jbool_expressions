package com.bpodgursky.jbool_expressions;

import java.util.List;

import com.bpodgursky.jbool_expressions.rules.Rule;

public abstract class Expression<K> implements Comparable<Expression> {

  public int compareTo(Expression o) {
    int compare = Integer.compare(hashCode(), o.hashCode());
    if(compare == 0 && !equals(o)) {
      // If hashcode matches and expressions are not equal then we may have a hash collision.
      // This is very unlikely to happen but if it does then go for string comparison (slow).
      return toString().compareTo(o.toString());
    }
    return compare;
  }

  public abstract Expression<K> apply(List<Rule<?, K>> rules);

  public abstract String getExprType();
}
