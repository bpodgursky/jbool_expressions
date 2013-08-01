package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import junit.framework.TestCase;

import java.util.List;

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

  public void assertSimplify(String expected, String orig){
    assertEquals(expected, simplifyToString(orig));
  }

  public void assertApply(String expected, String orig, List<Rule<?, String>> rules){
    assertEquals(expected, RuleSet.applyAll(expr(orig), rules).toString());
  }

  public void assertToSop(String expected, String orig){
    assertEquals(expected, toSOPString(orig));
  }
}
