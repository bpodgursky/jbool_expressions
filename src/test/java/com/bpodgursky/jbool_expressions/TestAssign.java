package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.rules.RulesHelper;
import junit.framework.TestCase;

import java.util.Collections;

public class TestAssign extends TestCase {

  public void testAssign(){

    And<String> expr3 = And.of(
        Or.of(Variable.of("A"), Variable.of("B")),
        Or.of(Variable.of("C"), Variable.of("D")
        )
    );

    Expression<String> expr = RuleSet.assign(expr3, Collections.singletonMap("A", false), ExprOptions.noCaching());
    assertEquals("(B & (C | D))", expr.toString());

    expr = RuleSet.assign(expr, Collections.singletonMap("B", true), ExprOptions.noCaching());
    assertEquals("(C | D)", expr.toString());

    expr = RuleSet.assign(expr, Collections.singletonMap("C", true), ExprOptions.noCaching());
    assertEquals(Literal.<String>getTrue(), expr);
  }
}
