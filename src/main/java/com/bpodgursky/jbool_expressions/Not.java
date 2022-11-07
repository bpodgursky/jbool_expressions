package com.bpodgursky.jbool_expressions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.PrintOptions.BooleanOperatorOption;
import com.bpodgursky.jbool_expressions.PrintOptions.ExpressionLayoutOption;
import com.bpodgursky.jbool_expressions.PrintOptions.WhitespaceOption;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.rules.RulesHelper;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public class Not<K> extends Expression<K> {
  public static final String EXPR_TYPE = "not";
  private String cachedStringRepresentation = null;

  private final Expression<K> e;

  private Not(Expression<K> e) {
    this.e = e;
  }

  public Expression<K> getE() {
    return e;
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
  
    String whitespace = null;

    if (whitespaceOption == WhitespaceOption.AS_SPACE) {
      whitespace = " ";
    } else if (whitespaceOption == WhitespaceOption.AS_TAB) {
      whitespace = "\t";
    } else {
      throw new UnsupportedOperationException("Unsupported WhitespaceOption: " + whitespaceOption);
    }
    
    ExpressionLayoutOption expressionLayoutOption = options.getExpressionLayoutOption();

    if (booleanOperatorOption == BooleanOperatorOption.AS_SYMBOL) {
      if (expressionLayoutOption == ExpressionLayoutOption.PRETTY_PRINT) {
        final String indentation = new String(new char[options.getIndentationCount()]).replace("\0", whitespace);
        return "!\n" + indentation + e.toString(options).replace("\n", "\n" + indentation);
      } else if (expressionLayoutOption == ExpressionLayoutOption.DEFAULT) {
          return "!" + e.toString(options);
      } else {
          throw new UnsupportedOperationException("Unsupported ExpressionLayoutOption: " + expressionLayoutOption);
      }
    } else if (booleanOperatorOption == BooleanOperatorOption.AS_ENGLISH_TEXT_LOWERCASE) {
      if (expressionLayoutOption == ExpressionLayoutOption.PRETTY_PRINT) {
        final String indentation = new String(new char[options.getIndentationCount()]).replace("\0", whitespace);
        return "not\n" + indentation + e.toString(options).replace("\n", "\n" + indentation);
      } else if (expressionLayoutOption == ExpressionLayoutOption.DEFAULT) {
          return "not" + whitespace + e.toString(options);
      } else {
          throw new UnsupportedOperationException("Unsupported ExpressionLayoutOption: " + expressionLayoutOption);
      }
    } else if (booleanOperatorOption == BooleanOperatorOption.AS_ENGLISH_TEXT_UPPERCASE) {
      if (expressionLayoutOption == ExpressionLayoutOption.PRETTY_PRINT) {
        final String indentation = new String(new char[options.getIndentationCount()]).replace("\0", whitespace);
        return "NOT\n" + indentation + e.toString(options).replace("\n", "\n" + indentation);
      } else if (expressionLayoutOption == ExpressionLayoutOption.DEFAULT) {
        return "NOT" + whitespace + e.toString(options);
      } else {
        throw new UnsupportedOperationException("Unsupported ExpressionLayoutOption: " + expressionLayoutOption);
      }
    } else if (booleanOperatorOption == BooleanOperatorOption.AS_ENGLISH_TEXT_CAPITALIZE) {
      if (expressionLayoutOption == ExpressionLayoutOption.PRETTY_PRINT) {
        final String indentation = new String(new char[options.getIndentationCount()]).replace("\0", whitespace);
        return "Not\n" + indentation + e.toString(options).replace("\n", "\n" + indentation);
      } else if (expressionLayoutOption == ExpressionLayoutOption.DEFAULT) {
          return "Not" + whitespace + e.toString(options);
      } else {
          throw new UnsupportedOperationException("Unsupported ExpressionLayoutOption: " + expressionLayoutOption);
      }
    } else {
      throw new UnsupportedOperationException("Unsupported BooleanOperatorOption: " + booleanOperatorOption);
    }
  }
  
  @Override
  public Expression<K> apply(RuleList<K> rules, ExprOptions<K> options) {
    Expression<K> e = RulesHelper.applyAll(this.e, rules, options);

    if(e != this.e){
      return options.getExprFactory().not(e);
    }

    return this;
  }

  @Override
  public List<Expression<K>> getChildren() {
    return Collections.singletonList(e);
  }

  @Override
  public Expression<K> map(Function<Expression<K>, Expression<K>> function, ExprFactory<K> factory) {

    Expression<K> map = this.e.map(function, factory);

    if(map != this.e){
      return function.apply(factory.not(map));
    }

    return function.apply(this);
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
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Not<?> not = (Not<?>)o;
    return Objects.equals(e, not.e);
  }

  @Override
  public int hashCode() {
    return Objects.hash(e);
  }

  public Set<K> getAllK() {
    return e.getAllK();
  }

  @Override
  public void collectK(Set<K> set, int limit) {
    e.collectK(set, limit);
  }

  public Expression<K> replaceVars(Map<K, Expression<K>> m, ExprFactory<K> factory) {
    return of(e.replaceVars(m, factory));
  }
}
