package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.DeMorgan;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.rules.RulesHelper;

import java.util.Arrays;

import static org.junit.Assert.assertNotEquals;

public class TestNot extends JBoolTestCase {

  public void testSimplify(){

    assertSimplify("!E", "(! E)");
    assertSimplify("E", "(! (! E))");
    assertSimplify("false", "(! true)");
    assertSimplify("E", "(! (! (! (! (! (! (! (! E))))))))");

  }

  public void testNot3(){
    assertSimplify("!E", "(! ( E & E))");
  }

  public void testDeMorgan(){

    Expression<String> after1 = ExprParser.parse("( (! A) & (! B))");
    Expression<String> expr1 = ExprParser.parse("(! ( A | B))");

    assertEquals(after1.toString(),
        RulesHelper.<String>applySet(expr1, Arrays.<Rule<?, String>>asList(new DeMorgan<String>())).toString());

    Expression<String> expr2 = ExprParser.parse("(! ( A&  B))");
    Expression<String> after2 = ExprParser.parse("( (! A)|  (! B))");

    assertEquals(after2.toString(),
        RulesHelper.<String>applySet(expr2, Arrays.<Rule<?, String>>asList(new DeMorgan<String>())).toString());

    Not<String> n3 = Not.of(Variable.of("b"));
    assertEquals(n3, RuleSet.simplify(n3));

    assertSimplify("!B", "(! B)");

  }

  public void testEqualsHashCode() {
    assertNotEquals(Variable.of("E"), Not.of(Variable.of("E")));
    assertNotEquals(Variable.of("E").hashCode(), Not.of(Variable.of("E")).hashCode());
  }

}
