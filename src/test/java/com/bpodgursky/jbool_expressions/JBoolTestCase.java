package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.rules.RuleSet;
import junit.framework.TestCase;

public abstract class JBoolTestCase extends TestCase {
  public static Expression<String> expr(String expr){
    return PrefixParser.parse(expr);
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

  public void assertToSop(String expected, String orig){
    assertEquals(expected, toSOPString(orig));
  }
}
