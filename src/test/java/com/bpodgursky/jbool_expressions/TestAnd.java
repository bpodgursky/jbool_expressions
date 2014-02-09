package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.parsers.ExprParser;

public class TestAnd extends JBoolTestCase {

  public void testSimplify(){
    assertSimplify("A", "( A & A)");
    assertSimplify("A", "( A & A & A)");
    assertSimplify("false", "( A& A & (! A))");
    assertSimplify("A", "( A&  A & true)");
    assertSimplify("false", "( A&  A & false)");
  }

  public void testAndTrue(){
    assertSimplify("true", "( true & (! (! true)))");
  }

  public void testCombineAnd(){
    assertSimplify("(A & B)","( A & B & A)");
  }

  public void testToSopOnce(){
    assertToSop("((A & C) | (A & D))", "( A & ( C | D))");
    assertToSop("((A & C) | (A & D) | (B & C) | (B & D))", "( ( A|  B) & ( C|  D))");
  }

  public void testToPos(){
    assertToPos("((A | C) & B & D)", "(A & B & D) | (B & C & D)");
    assertToPos("(!C | !D | A)", "((A & !B) | (A & D) | (!C & D) | !D)");
    assertEvaluateSame(
        ExprParser.parse("((A & !B) | (A & D) | (!C & D) | !D)"),
        ExprParser.parse("(!C | !D | A)")
    );
  }

}
