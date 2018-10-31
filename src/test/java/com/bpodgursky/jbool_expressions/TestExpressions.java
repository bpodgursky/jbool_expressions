package com.bpodgursky.jbool_expressions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bpodgursky.jbool_expressions.eval.EvalEngine;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

public class TestExpressions extends JBoolTestCase {

  public void testToStr(){

    Expression<String> expr = And.of(
        Or.of(Variable.of("C"), Variable.of("D"), Literal.<String>getFalse()),
        Not.of(Or.of(Variable.of("A"),Literal.<String>getTrue())),
        Variable.of("F")
    );

    assertEquals("(F & !(A | true) & (C | D | false))", expr.toString());

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

    Map<String, Boolean> values1 = new HashMap<>();
    values1.put("A", true);
    assertEquals(true, EvalEngine.evaluateBoolean(expr, values1));

    Map<String, Boolean> values2 = new HashMap<>();
    values2.put("A", false);
    assertEquals(false, EvalEngine.evaluateBoolean(expr, values2));

    Map<String, Boolean> values3 = new HashMap<>();
    values3.put("A", false);
    values3.put("B", true);
    assertEquals(false, EvalEngine.evaluateBoolean(expr1, values3));

    Map<String, Boolean> values4 = new HashMap<>();
    values4.put("A", true);
    values4.put("B", true);
    assertEquals(true, EvalEngine.evaluateBoolean(expr1, values4));

    Map<String, Boolean> values5 = new HashMap<>();
    values5.put("A", true);
    values5.put("B", false);
    assertEquals(true, EvalEngine.evaluateBoolean(expr3, values5));

    Map<String, Boolean> values6 = new HashMap<>();
    values6.put("A", false);
    values6.put("B", false);
    assertEquals(false, EvalEngine.evaluateBoolean(expr3, values6));

    Map<String, Boolean> values7 = new HashMap<>();
    values7.put("A", false);
    assertEquals(true, EvalEngine.evaluateBoolean(expr4, values7));

    Map<String, Boolean> values8 = new HashMap<>();
    values8.put("B", true);
    assertEquals(true, EvalEngine.evaluateBoolean(expr3, values8));

  }

  public void testReplacement(){

    Expression<String> expr = expr("(F & !D & (C | !A ))");

    assertEquals(new HashSet<>(Arrays.asList("A", "C", "D", "F")), expr.getAllK());

    assertEquals(
        "(F & !D & (C | !(B & C)))",
        expr.replaceVars(Collections.singletonMap("A", expr("(B & C)")), new ExprFactory.Interning<>(new HashMap<>())).toString()
    );

  }

  private enum TestEnum {
    A
  }

  public void testEnum(){

    Expression<TestEnum> expr = And.of(Not.of(Variable.of(TestEnum.A)));
    assertEquals("!A", RuleSet.simplify(expr).toString());
  }

  public void testParse() throws Exception {
    assertSimplify("aBC_D9", "aBC_D9 & (aBC_D9 | BCD)");
    assertSimplify("'not even valid'", "'not even valid' & ('not even valid' | BCD)");
  }

  public void testLongExpression() {
      String exp = "(122036 | 122037 | 122039 | 122040 | 122042 | 122043 | 122045 | 122046 | 122048 | 122049 | " +
              "122051 | 122052 | 122054 | 122055 | 122057 | 122058 | 122060 | 122061 | 122063 | 122064 | 122066 | " +
              "122067 | 122069 | 122070 | 169225 | 169226 | 169228 | 169229 | 169231 | 169232 | 169234 | 169235 | " +
              "169237 | 169238 | 169240 | 169241 | 169243 | 169244 | 169246 | 169247 | 169249 | 169250 | 169252 | " +
              "169253 | 169255 | 169256 | 169258 | 169259 | 169261 | 169262 | 169264 | 169265 | 106925 | 106926 | " +
              "165611)";
      ExprParser.parse(exp);
      ExprParser.parse(exp.replaceAll("\\|","&"));
      ExprParser.parse(exp.replaceAll("\\|","& !"));
  }

}
