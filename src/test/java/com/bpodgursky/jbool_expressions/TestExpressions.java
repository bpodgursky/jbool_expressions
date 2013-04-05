package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.eval.EvalEngine;
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
    assertEquals(true, EvalEngine.evaluateBoolean(expr, values1));

    Map<String, Boolean> values2 = Maps.newHashMap();
    values2.put("A", false);
    assertEquals(false, EvalEngine.evaluateBoolean(expr, values2));

    Map<String, Boolean> values3 = Maps.newHashMap();
    values3.put("A", false);
    values3.put("B", true);
    assertEquals(false, EvalEngine.evaluateBoolean(expr1, values3));

    Map<String, Boolean> values4 = Maps.newHashMap();
    values4.put("A", true);
    values4.put("B", true);
    assertEquals(true, EvalEngine.evaluateBoolean(expr1, values4));

    Map<String, Boolean> values5 = Maps.newHashMap();
    values5.put("A", true);
    values5.put("B", false);
    assertEquals(true, EvalEngine.evaluateBoolean(expr3, values5));

    Map<String, Boolean> values6 = Maps.newHashMap();
    values6.put("A", false);
    values6.put("B", false);
    assertEquals(false, EvalEngine.evaluateBoolean(expr3, values6));

    Map<String, Boolean> values7 = Maps.newHashMap();
    values7.put("A", false);
    assertEquals(true, EvalEngine.evaluateBoolean(expr4, values7));

  }

  private enum TestEnum {
    A
  }

  public void testEnum(){

    Expression<TestEnum> expr = And.of(Not.of(Variable.of(TestEnum.A)));
    assertEquals("!A", RuleSet.simplify(expr).toString());
  }

  private static abstract class Condition {
    public final String variable;

    protected Condition(String variable) {
      this.variable = variable;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Condition)) return false;

      Condition condition = (Condition) o;

      if (variable != null ? !variable.equals(condition.variable) : condition.variable != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return variable != null ? variable.hashCode() : 0;
    }
  }

  private static class LessThan extends Condition {
    public final long threshold;

    private LessThan(String variable, long threshold) {
      super(variable);
      this.threshold = threshold;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof LessThan)) return false;
      if (!super.equals(o)) return false;

      LessThan lessThan = (LessThan) o;

      if (threshold != lessThan.threshold) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (int) (threshold ^ (threshold >>> 32));
      return result;
    }
  }

  private static class GreaterThan extends Condition {
    public final long threshold;

    private GreaterThan(String variable, long threshold) {
      super(variable);
      this.threshold = threshold;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof GreaterThan)) return false;
      if (!super.equals(o)) return false;

      GreaterThan that = (GreaterThan) o;

      if (threshold != that.threshold) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (int) (threshold ^ (threshold >>> 32));
      return result;
    }
  }

  public void testWithOtherVars(){

    //
    Condition cond1 = new LessThan("A", 2);
    Condition cond2 = new GreaterThan("B", 3);

    Expression<Condition> cond = And.of(Variable.of(cond1), Variable.of(cond2));

    //   idea of a state
    //  create it with a set of conditions to keep track of
    //  put in observations
    //  emit conditions which are now true

    //  so
    //  trie is built out of facts F
    //  all facts in your expression are tracked in the state

    //

    //

    //  (a | b | c) && !a

    //  unification

    //  Trie<K>
    //  State<K, D>
    //  Expression<D>

    //  every time an item is skipped in the trie, need to record ! that item in the state.  The state returns conditions that are now true
      //  inform(K item, boolean state)



  }
}
