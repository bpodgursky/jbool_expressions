package com.bpodgursky.jbool_expressions;

import java.util.Arrays;

import com.bpodgursky.jbool_expressions.options.ExprOptions;

public class TestExprUtil extends JBoolTestCase {

  public void testMinConstraint() {

    assertEquals(Arrays.asList("B", "A"), ExprUtil.getConstraintsByWeight(expr("(A & B) | B"), ExprOptions.noCaching()));
    assertEquals(Arrays.asList("B", "A", "C"), ExprUtil.getConstraintsByWeight(expr("(A & B) | (B & C)"), ExprOptions.noCaching()));
    assertEquals(Arrays.asList("D", "B", "A", "C"), ExprUtil.getConstraintsByWeight(expr("(A & B) | (B & C) | !D"), ExprOptions.noCaching()));

  }

}