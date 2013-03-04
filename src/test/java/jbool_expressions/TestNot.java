package jbool_expressions;

import jbool_expressions.rules.DeMorgan;
import jbool_expressions.rules.Rule;
import jbool_expressions.rules.RuleSet;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class TestNot extends JBoolTestCase {

  public void testSimplify(){

    assertSimplify("(! E)", "(! E)");
    assertSimplify("E", "(! (! E))");
    assertSimplify("false", "(! true)");
    assertSimplify("E", "(! (! (! (! (! (! (! (! E))))))))");

  }

  public void testNot3(){
    assertSimplify("(! E)", "(! (* E))");
  }

  public void testDeMorgan(){

    Expression<String> after1 = PrefixParser.parse("(* (! A) (! B))");
    Expression<String> expr1 = PrefixParser.parse("(! (+ A B))");

    assertEquals(after1.toString(),
        RuleSet.<String>applySet(expr1, Arrays.<Rule<?, String>>asList(new DeMorgan<String>())).toString());

    Expression<String> expr2 = PrefixParser.parse("(! (* A B))");
    Expression<String> after2 = PrefixParser.parse("(+ (! A) (! B))");

    assertEquals(after2.toString(),
        RuleSet.<String>applySet(expr2, Arrays.<Rule<?, String>>asList(new DeMorgan<String>())).toString());

    Not<String> n3 = Not.of(Variable.of("b"));
    assertEquals(n3, RuleSet.simplify(n3));

    assertSimplify("(! B)", "(! B)");

  }

}
