package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import org.junit.jupiter.api.Test;

public class TestAnd extends JBoolTestCase {

  @Test
  public void testSimplify() {
    assertSimplify("A", "( A & A)");
    assertSimplify("A", "( A & A & A)");
    assertSimplify("false", "( A& A & (! A))");
    assertSimplify("A", "( A&  A & true)");
    assertSimplify("false", "( A&  A & false)");
  }

  @Test
  public void testAndTrue() {
    assertSimplify("true", "( true & (! (! true)))");
  }

  @Test
  public void testCombineAnd() {
    assertSimplify("(A & B)", "( A & B & A)");
  }

  @Test
  public void testToSopOnce() {
    assertToSop("((A & C) | (A & D))", "( A & ( C | D))");
    assertToSop("((A & C) | (A & D) | (B & C) | (B & D))", "( ( A|  B) & ( C|  D))");
  }

  @Test
  public void testToPos() {
    assertToPos("(B & D & (A | C))", "(A & B & D) | (B & C & D)");
    assertToPos("(A | !C | !D)", "((A & !B) | (A & D) | (!C & D) | !D)");
    assertEvaluateSame(
        ExprParser.parse("((A & !B) | (A & D) | (!C & D) | !D)"),
        ExprParser.parse("(!C | !D | A)")
    );

    assertToPos("(B & D & (A | C))", "(A & B & D) | (B & C & D)");
  }

  @Test
  public void testToSopPerformance() {
    assertToPos("(N & (A | B) & (K | L | M) & (C | D | E | F))", "((A | B) & (C | D | E | F) & (K | L | M) & N)");
  }

  @Test
  public void testSimplifyChildren() {
    assertSimplify("(A | B)", "(A | B) & (A | B | C)");
    assertSimplify("(A & B)", "((A & B) | (A & B & C))");
  }

  @Test
  public void testEqualsHashCode() {
    assertEquals(And.of(Variable.of("A"), Variable.of("B")), And.of(Variable.of("B"), Variable.of("A")));
    assertEquals(And.of(Variable.of("A"), Variable.of("B")).hashCode(), And.of(Variable.of("B"), Variable.of("A")).hashCode());
    assertEquals(And.of(Variable.of("A"), Variable.of("B")).hashCode(), And.of(Variable.of("A"), Variable.of("B")).hashCode());
    assertNotEquals(And.of(Variable.of("A"), Variable.of("B")), And.of(Variable.of("B"), Variable.of("C")));
    assertNotEquals(And.of(Variable.of("A"), Variable.of("B")).hashCode(), And.of(Variable.of("A"), Variable.of("C")).hashCode());
    assertNotEquals(And.of(Variable.of("A"), Variable.of("B")).hashCode(), Or.of(Variable.of("A"), Variable.of("B")).hashCode());
  }

  @Test
  public void testConstructionViaArray() {
    assertEquals("(A & B & C & D & E)", And.of(Variable.of("A"), Variable.of("B"), Variable.of("C"), Variable.of("D"), Variable.of("E")).toString());
  }
}
