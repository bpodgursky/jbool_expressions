package com.bpodgursky.jbool_expressions;

import org.junit.jupiter.api.Test;

public class TestOr extends JBoolTestCase {

  @Test
  public void testSimplify(){
    assertSimplify("A", "( A| A)");
    assertSimplify("A", "( A|  A | A)");
    assertSimplify("true", "( A| A | (! A))");
    assertSimplify("true", "( A|  A |true)");
    assertSimplify("A", "( A|  A | false)");
  }

  @Test
  public void testAndTrue(){
    assertSimplify("true", "( true | (! (! true)))");
    assertSimplify("true", "( true  | (! (! false)))");
  }

  @Test
  public void testTwoVar(){
    assertSimplify("(A | B)", "( A | B | A)");
  }

  @Test
  public void testNExpr(){
    assertSimplify("true", "( A | B | A | ( true & (! (! true))))");
  }

  @Test
  public void testEqualsHashCode() {
    assertEquals(Or.of(Variable.of("A"), Variable.of("B")), Or.of(Variable.of("B"), Variable.of("A")));
    assertEquals(Or.of(Variable.of("A"), Variable.of("B")).hashCode(), Or.of(Variable.of("B"), Variable.of("A")).hashCode());
    assertEquals(Or.of(Variable.of("A"), Variable.of("B")).hashCode(), Or.of(Variable.of("A"), Variable.of("B")).hashCode());
    assertNotEquals(Or.of(Variable.of("A"), Variable.of("B")), Or.of(Variable.of("B"), Variable.of("C")));
    assertNotEquals(Or.of(Variable.of("A"), Variable.of("B")).hashCode(), Or.of(Variable.of("A"), Variable.of("C")).hashCode());
    assertNotEquals(Or.of(Variable.of("A"), Variable.of("B")).hashCode(), And.of(Variable.of("A"), Variable.of("B")).hashCode());
  }

  @Test
  public void testConstructionViaArray() {
    assertEquals("(A | B | C | D | E)", Or.of(Variable.of("A"), Variable.of("B"), Variable.of("C"), Variable.of("D"), Variable.of("E")).toString());
  }
}
