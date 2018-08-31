package com.bpodgursky.jbool_expressions;

import java.util.Arrays;

public class TestExprUtil extends JBoolTestCase {

  public void testMinConstraint() {

    assertEquals(Arrays.asList("B", "A"), ExprUtil.getConstraintsByWeight(expr("(A & B) | B")));
    assertEquals(Arrays.asList("B", "A", "C"), ExprUtil.getConstraintsByWeight(expr("(A & B) | (B & C)")));
    assertEquals(Arrays.asList("D", "B", "A", "C"), ExprUtil.getConstraintsByWeight(expr("(A & B) | (B & C) | !D")));

  }

}