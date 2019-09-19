package com.bpodgursky.jbool_expressions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import java.util.function.Function;
import java.util.stream.Collectors;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.PrintOptions.BooleanOperatorOption;
import com.bpodgursky.jbool_expressions.PrintOptions.WhitespaceOption;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.rules.RulesHelper;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import static com.bpodgursky.jbool_expressions.Seeds.OR_SEED;

public class Or<K> extends NExpression<K> {
  public static final String EXPR_TYPE = "or";
  private String cachedStringRepresentation = null;

  public static <K> Or<K> of(Expression<K>[] children, Comparator<Expression> comparator) {
    return new Or<K>(children, comparator);
  }

  private Or(Expression<K>[] children, Comparator<Expression> comparator) {
    super(children, OR_SEED, comparator);
  }

  public String toString() {
    if (cachedStringRepresentation == null) {
      cachedStringRepresentation = toString(PrintOptions.withDefaults());
    }
    return cachedStringRepresentation;
  }
  
  @Override
  public String toString(PrintOptions options) {
    BooleanOperatorOption booleanOperatorOption = options.getBooleanOperatorOption();
    WhitespaceOption whitespaceOption = options.getWhitespaceOption();

    String operator = null;
    String whitespace = null;

    if (whitespaceOption == WhitespaceOption.AS_SPACE) {
      whitespace = " ";
    } else if (whitespaceOption == WhitespaceOption.AS_TAB) {
      whitespace = "\t";
    } else {
      throw new UnsupportedOperationException("Unsupported WhitespaceOption: " + whitespaceOption);
    }
    
    if (booleanOperatorOption == BooleanOperatorOption.AS_SYMBOL) {
      operator = whitespace + "|" + whitespace;
    } else if (booleanOperatorOption == BooleanOperatorOption.AS_ENGLISH_TEXT_LOWERCASE) {
      operator = whitespace + "or" + whitespace;
    } else if (booleanOperatorOption == BooleanOperatorOption.AS_ENGLISH_TEXT_UPPERCASE) {
      operator = whitespace + "OR" + whitespace;
    } else if (booleanOperatorOption == BooleanOperatorOption.AS_ENGLISH_TEXT_CAPITALIZE) {
      operator = whitespace + "Or" + whitespace;
    } else {
      throw new UnsupportedOperationException("Unsupported BooleanOperatorOption: " + booleanOperatorOption);
    }
    return Arrays.stream(expressions).map(expression -> expression.toString(options)).collect(Collectors.joining(operator, "(", ")"));
  }
  
  @Override
  public Expression<K> apply(RuleList<K> rules, ExprOptions<K> options) {
    Expression<K>[] children = null;

    boolean modified = false;
    for (int i = 0; i < this.expressions.length; i++) {
      Expression<K> newChild = RulesHelper.applyAll(this.expressions[i], rules, options);

      if (newChild != this.expressions[i]) {
        modified = true;

        if (children == null) {
          children = new Expression[this.expressions.length];
        }

        children[i] = newChild;
      }

    }

    if (!modified) {
      return this;
    }

    //  backfill
    for (int i = 0; i < this.expressions.length; i++) {
      if (children[i] == null) {
        children[i] = this.expressions[i];
      }
    }

    return options.getExprFactory().or(children);
  }

  @Override
  public Expression<K> map(Function<Expression<K>, Expression<K>> function, ExprFactory<K> factory) {
    Expression<K>[] children = null;

    boolean modified = false;
    for (int i = 0; i < this.expressions.length; i++) {
      Expression<K> newChild = this.expressions[i].map(function, factory);

      if (newChild != this.expressions[i]) {
        modified = true;

        if (children == null) {
          children = new Expression[this.expressions.length];
        }

        children[i] = newChild;
      }

    }

    if (!modified) {
      return function.apply(this);
    }

    //  backfill
    for (int i = 0; i < this.expressions.length; i++) {
      if (children[i] == null) {
        children[i] = this.expressions[i];
      }
    }

    return function.apply(factory.or(children));
  }

  @Override
  public Expression<K> sort(Comparator<Expression> comparator) {

    Expression<K>[] children = new Expression[this.expressions.length];
    for (int i = 0; i < this.expressions.length; i++) {
      children[i] = expressions[i].sort(comparator);
    }

    return Or.of(children, comparator);
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

  public static <K> Or<K> of(List<? extends Expression<K>> children, Comparator<Expression> comparator) {
    return of(children.toArray(new Expression[children.size()]), comparator);
  }
  
  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }

  public Expression<K> replaceVars(Map<K, Expression<K>> m, ExprFactory<K> factory) {
    Expression<K>[] children = new Expression[this.expressions.length];
    for (int i = 0; i < this.expressions.length; i++) {
      children[i] = this.expressions[i].replaceVars(m, factory);
    }
    return factory.or(children);
  }
}
