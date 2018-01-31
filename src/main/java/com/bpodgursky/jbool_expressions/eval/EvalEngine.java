package com.bpodgursky.jbool_expressions.eval;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.Variable;

import java.util.HashMap;
import java.util.Map;

public class EvalEngine {

  public static <K> Map<String, EvalRule<K>> booleanRules(){
    Map<String, EvalRule<K>> rules = new HashMap();
    rules.put(And.EXPR_TYPE, new EvalAnd<K>());
    rules.put(Or.EXPR_TYPE, new EvalOr<K>());
    rules.put(Not.EXPR_TYPE, new EvalNot<K>());
    rules.put(Literal.EXPR_TYPE, new EvalLiteral<K>());
    return rules;
  }

  public static <K> boolean evaluateBoolean(Expression<K> expr, Map<K, Boolean> values){
    Map<String, EvalRule<K>> rules = booleanRules();
    rules.put(Variable.EXPR_TYPE, new EvalVariable<K>(values));

    return evaluate(expr, rules);
  }

  public static <K> boolean evaluate(Expression<K> expr, Map<String, EvalRule<K>> rules){
    return EvalRule.evaluateInternal(expr, rules);
  }
}
