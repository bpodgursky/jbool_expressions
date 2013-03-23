package com.bpodgursky.jbool_expressions;

import com.google.common.collect.Maps;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import junit.framework.TestCase;

import java.util.*;

public class TestExpressions extends TestCase {

  public void testToStr(){

    Expression<String> expr = And.of(
        Or.of(Variable.of("C"), Variable.of("D"), Literal.<String>getFalse()),
        Not.of(Or.of(Variable.of("A"),Literal.<String>getTrue())),
        Variable.of("F")
    );

    assertEquals("(!(A | true) & (C | D | false) & F)", expr.toString());

    Set<String> allVars = new HashSet<String>(Arrays.asList("A", "C", "D", "F"));
    assertEquals(allVars, ExprUtil.getVariables(expr));
  }

  public void testEvaluate(){

    Expression<String> expr = And.of(
        Variable.of("A")
    );

    Expression<String> expr1 = And.of(
        Variable.of("A"),
        Variable.of("B")
    );

    Expression<String> expr3 = Or.of(
        Variable.of("A"),
        Variable.of("B")
    );

    Expression<String> expr4 = Not.of(
        Variable.of("A")
    );

    Map<String, Boolean> values1 = Maps.newHashMap();
    values1.put("A", true);
    assertEquals(true, expr.evaluate(new EvaluationContext<String>(values1)));

    Map<String, Boolean> values2 = Maps.newHashMap();
    values2.put("A", false);
    assertEquals(false, expr.evaluate(new EvaluationContext<String>(values2)));

    Map<String, Boolean> values3 = Maps.newHashMap();
    values3.put("A", false);
    values3.put("B", true);
    assertEquals(false, expr1.evaluate(new EvaluationContext<String>(values3)));

    Map<String, Boolean> values4 = Maps.newHashMap();
    values4.put("A", true);
    values4.put("B", true);
    assertEquals(true, expr1.evaluate(new EvaluationContext<String>(values4)));

    Map<String, Boolean> values5 = Maps.newHashMap();
    values5.put("A", true);
    values5.put("B", false);
    assertEquals(true, expr3.evaluate(new EvaluationContext<String>(values5)));

    Map<String, Boolean> values6 = Maps.newHashMap();
    values6.put("A", false);
    values6.put("B", false);
    assertEquals(false, expr3.evaluate(new EvaluationContext<String>(values6)));

    Map<String, Boolean> values7 = Maps.newHashMap();
    values7.put("A", false);
    assertEquals(true, expr4.evaluate(new EvaluationContext<String>(values7)));

  }

  private enum TestEnum {
    A
  }

  public void testEnum(){

    Expression<TestEnum> expr = And.of(Not.of(Variable.of(TestEnum.A)));
    assertEquals("!A", RuleSet.simplify(expr).toString());
  }
}
