package com.bpodgursky.jbool_expressions;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public abstract class Expression<K> implements Serializable {

  public static final Comparator<Expression> HASH_COMPARATOR = new HashComparator();

  public static final Comparator<Expression> LEXICOGRAPHIC_COMPARATOR = new LexicographicComparator();

  public static class HashComparator implements Comparator<Expression>  {
    @Override
    public int compare(Expression o1, Expression o2) {

      if(o1 == o2){
        return 0;
      }

      int compare = Integer.compare(o1.hashCode(), o2.hashCode());
      if(compare == 0 && !o1.equals(o2)) {
        // If hashcode matches and expressions are not equal then we may have a hash collision.
        // This is very unlikely to happen but if it does then go for string comparison (slow).
        return LEXICOGRAPHIC_COMPARATOR.compare(o1, o2);
      }
      return compare;
    }
  }

  public static class LexicographicComparator implements Comparator<Expression> {
    @Override
    public int compare(Expression o1, Expression o2) {
      return o1.toString().compareTo(o2.toString());
    }
  }

  public abstract Expression<K> apply(List<Rule<?, K>> rules, ExprOptions<K> cache);

  public abstract List<Expression<K>> getChildren();

  public abstract Expression<K> map(Function<Expression<K>, Expression<K>> function, ExprFactory<K> factory);

  public abstract String getExprType();

  protected abstract Expression<K> sort(Comparator<Expression> comparator);

  public String toLexicographicString(){
    return sort(LEXICOGRAPHIC_COMPARATOR).toString();
  }

  private transient Set<K> k = null;

  public Set<K> getAllK(){

    if(k != null){
      return k;
    }

    Set<K> variables =new HashSet<>();
    collectK(variables, Integer.MAX_VALUE);
    this.k = variables;
    return variables;
  }

  public abstract void collectK(Set<K> set, int limit);

  public abstract Expression<K> replaceVars(Map<K,Expression<K>> m, ExprFactory<K> exprFactory);
}
