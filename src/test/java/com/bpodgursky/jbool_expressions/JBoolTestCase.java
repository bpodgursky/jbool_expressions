package com.bpodgursky.jbool_expressions;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.TestCase;

public abstract class JBoolTestCase extends TestCase {
  public static Expression<String> expr(String expr){
    return ExprParser.parse(expr);
  }

  public static String simplifyToString(String expr){
    return RuleSet.simplify(expr(expr)).toString();
  }

  public static String toSOPString(String expr){
    return RuleSet.toSop(expr(expr)).toString();
  }

  public static String toPOSString(String expr){
    return RuleSet.toPos(expr(expr)).toString();
  }


  public void assertSimplify(String expected, String orig){
    assertEquals(expected, simplifyToString(orig));
  }

  public void assertApply(String expected, String orig, List<Rule<?, String>> rules){
    assertEquals(expected, RuleSet.applyAll(expr(orig), rules).toString());
  }

  public void assertToSop(String expected, String orig){
    assertEquals(expected, toSOPString(orig));
  }

  public void assertToPos(String expected, String orig){
    assertEquals(expected, toPOSString(orig));
  }


  public <K> void assertEvaluateSame(Expression<K> s1, Expression<K> s2){

    Set<K> variables = ExprUtil.getVariables(s1);
    variables.addAll(ExprUtil.getVariables(s2));

    evaluate(Lists.newLinkedList(variables), Maps.<K, Boolean>newHashMap(), s1, s2);

  }

  public <K> void evaluate(Queue<K> variables, Map<K, Boolean> assignments, Expression<K> s1, Expression<K> s2){
    Map<K, Boolean> assignmentCopy = Maps.newHashMap(assignments);
    Queue<K> variableCopy = Lists.newLinkedList(variables);

    if(variableCopy.isEmpty()){
      Expression<K> s1Eval = RuleSet.assign(s1, assignmentCopy);
      Expression<K> s2Eval = RuleSet.assign(s2, assignmentCopy);

      assertEquals(s1Eval, s2Eval);
    }
    else{
      K assign = variableCopy.poll();

      assignmentCopy.put(assign, true);
      evaluate(variableCopy, assignmentCopy, s1, s2);

      assignmentCopy.put(assign, false);
      evaluate(variableCopy, assignmentCopy, s1, s2);

    }
  }
}
