package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.DeMorgan;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.rules.RulesHelper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class TestNot extends JBoolTestCase {

  @Test
  public void testSimplify(){
    assertSimplify("!E", "(! E)");
    assertSimplify("E", "(! (! E))");
    assertSimplify("false", "(! true)");
    assertSimplify("E", "(! (! (! (! (! (! (! (! E))))))))");
  }

  @Test
  public void testNot3(){
    assertSimplify("!E", "(! ( E & E))");
  }

  @Test
  public void testDeMorgan(){
    Expression<String> after1 = ExprParser.parse("( (! A) & (! B))");
    Expression<String> expr1 = ExprParser.parse("(! ( A | B))");

    assertEquals(after1.toString(),
        RulesHelper.applySet(expr1, new RuleList<>(Collections.singletonList(new DeMorgan<>())), ExprOptions.noCaching()).toString());

    Expression<String> expr2 = ExprParser.parse("(! ( A&  B))");
    Expression<String> after2 = ExprParser.parse("( (! A)|  (! B))");

    assertEquals(after2.toString(),
        RulesHelper.applySet(expr2, new RuleList<>(Collections.singletonList(new DeMorgan<>())), ExprOptions.noCaching()).toString());

    Not<String> n3 = Not.of(Variable.of("b"));
    assertEquals(n3, RuleSet.simplify(n3));

    assertSimplify("!B", "(! B)");
  }

  @Test
  public void testEqualsHashCode() {
    assertNotEquals(Variable.of("E"), Not.of(Variable.of("E")));
    assertNotEquals(Variable.of("E").hashCode(), Not.of(Variable.of("E")).hashCode());
  }
}
