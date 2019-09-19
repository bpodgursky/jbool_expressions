package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.PrintOptions.VariableEscapeApplyOption;
import com.bpodgursky.jbool_expressions.PrintOptions.VariableEscapeOption;
import com.bpodgursky.jbool_expressions.PrintOptions.VariableNameOption;
import com.bpodgursky.jbool_expressions.cache.RuleSetCache;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class Variable<K> extends Expression<K> {
  public static final String EXPR_TYPE = "variable";

  private final String name;
  private final String escapeSymbol;
  private final K value;

  private Variable(K value) {
    this((value != null ? value.toString() : null), value);  
  }

  private Variable(String name, K value) {
    if (name != null) {
      if (name.startsWith("'")) {
        this.escapeSymbol = "'";
        name = name.substring(1); // remove first ` character
        name = name.substring(0, name.length() - 1); // remove second ` character
        name = ExprParser.unescapeVariableName(name);
      } else if (name.startsWith("\"")) {
        this.escapeSymbol = "\"";
        name = name.substring(1); // remove first ` character
        name = name.substring(0, name.length() - 1); // remove second ` character
        name = ExprParser.unescapeVariableName(name);
      } else if (name.startsWith("`")) {
        this.escapeSymbol = "`";
        name = name.substring(1); // remove first ` character
        name = name.substring(0, name.length() - 1); // remove second ` character
        name = ExprParser.unescapeVariableName(name);
      } else {
        this.escapeSymbol = null;
      }
    } else {
      this.escapeSymbol = null;
    }
    this.value = value;
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public K getValue() {
    return value;
  }

  public String toString() {
    return toString(PrintOptions.withDefaults());
  }

  @Override
  public String toString(PrintOptions options) {
    VariableEscapeOption variableEscapeOption = options.getVariableEscapeOption();
    VariableEscapeApplyOption variableEscapeApplyOption = options.getVariableEscapeApplyOption();
    VariableNameOption variableNameOption = options.getVariableNameOption();

    String variableEscape = null;

    if (variableEscapeOption == VariableEscapeOption.AS_CREATED) {
      variableEscape = escapeSymbol; 
    } else if (variableEscapeOption == VariableEscapeOption.AS_QUOTED_STRING) {
      variableEscape = "'";
    } else if (variableEscapeOption == VariableEscapeOption.AS_DOUBLE_QUOTED_STRING) {
      variableEscape = "\"";
    } else if (variableEscapeOption == VariableEscapeOption.AS_BACKTICK_STRING) {
      variableEscape = "`";
    } else {
      throw new UnsupportedOperationException("Unsupported VariableEscapeOption: " + variableEscapeOption);
    }

    if (variableEscapeApplyOption == VariableEscapeApplyOption.AS_CREATED) {
      String variableName = null;
      
      if (variableNameOption == VariableNameOption.AS_CREATED) {
        variableName = name;
      } else if (variableNameOption == VariableNameOption.AS_UPPERCASE) {
        variableName = name.toUpperCase();
      } else if (variableNameOption == VariableNameOption.AS_LOWERCASE) {
        variableName = name.toLowerCase();
      } else if (variableNameOption == VariableNameOption.AS_CAPITALIZE) {
        variableName = (name.length() > 1 ? (name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase()) : name.toUpperCase());
      } else {
         throw new UnsupportedOperationException("Unsupported VariableNameOption: " + variableNameOption);
      }
      
      if (variableEscape != null) {
        variableName = variableEscape + ExprParser.escapeVariableName(variableName).replace(variableEscape, "\\" + variableEscape) + variableEscape;
      } else {
        variableName = (ExprParser.isValidVariableName(variableName) ? ExprParser.escapeVariableName(variableName) : ("'" + ExprParser.escapeVariableName(variableName).replace("'", "\\'") + "'"));
      }
      
      return variableName;
    } else if (variableEscapeApplyOption == VariableEscapeApplyOption.ONLY_WHEN_NEEDED) {
      String variableName = null;
      
      if (variableNameOption == VariableNameOption.AS_CREATED) {
        variableName = name;
      } else if (variableNameOption == VariableNameOption.AS_UPPERCASE) {
        variableName = name.toUpperCase();
      } else if (variableNameOption == VariableNameOption.AS_LOWERCASE) {
        variableName = name.toLowerCase();
      } else if (variableNameOption == VariableNameOption.AS_CAPITALIZE) {
        variableName = (name.length() > 1 ? (name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase()) : name.toUpperCase());
      } else {
         throw new UnsupportedOperationException("Unsupported VariableNameOption: " + variableNameOption);
      }
        
      variableName = (ExprParser.isValidVariableName(variableName) ? ExprParser.escapeVariableName(variableName) : (variableEscape + ExprParser.escapeVariableName(variableName).replace(variableEscape, "\\" + variableEscape) + variableEscape));
      
      return variableName;
    } else if (variableEscapeApplyOption == VariableEscapeApplyOption.ALWAYS) {
      String variableName = null;
      
      if (variableNameOption == VariableNameOption.AS_CREATED) {
        variableName = name;
      } else if (variableNameOption == VariableNameOption.AS_UPPERCASE) {
        variableName = name.toUpperCase();
      } else if (variableNameOption == VariableNameOption.AS_LOWERCASE) {
        variableName = name.toLowerCase();
      } else if (variableNameOption == VariableNameOption.AS_CAPITALIZE) {
        variableName = (name.length() > 1 ? (name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase()) : name.toUpperCase());
      } else {
        throw new UnsupportedOperationException("Unsupported VariableNameOption: " + variableNameOption);
      }
        
      if (variableEscape == null) { // in case VariableEscapeOption.AS_CREATED
        variableEscape = "'";
      }
      
      variableName = variableEscape + ExprParser.escapeVariableName(variableName).replace(variableEscape, "\\" + variableEscape) + variableEscape;
      
      return variableName;
    } else {
      throw new UnsupportedOperationException("Unsupported VariableEscapeApplyOption: " + variableEscapeApplyOption);
    }
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

  public static <K> Variable<K> of(K value) {
    return new Variable<K>(value != null ? value.toString() : null, value);
  }

  public static <K> Variable<K> of(String name, K value) {
    return new Variable<K>(name, value);
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
    Variable<?> variable = (Variable<?>)o;
    return Objects.equals(value, variable.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public void collectK(Set<K> set, int limit) {

    if(set.size() >= limit){
      return;
    }

    set.add(value);
  }

  public Expression<K> replaceVars(Map<K, Expression<K>> m, ExprFactory<K> factory) {
    if (m.containsKey(getValue())) {
      return m.get(getValue());
    }
    return this;
  }
}
