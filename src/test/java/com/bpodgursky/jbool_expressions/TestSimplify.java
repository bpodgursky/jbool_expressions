package com.bpodgursky.jbool_expressions;

public class TestSimplify extends JBoolTestCase {

  public void testSimplify(){

    //  A | (A & B)  = A
    assertSimplify("A", "A | (A & B)");

    //  A & (A | B) = A
    assertSimplify("A", "A & (A | B)");

    //  other more nested cases
    assertSimplify("A", "A & (A | B | C)");
    assertSimplify("(A & C)", "(A & C) | ((A & C) & B & D)");
    assertSimplify("(A & C)", "A & C | (A & C & B & D)");
    assertSimplify("(A | C)", "(A | C) & ((A | C) | B | D)");
    assertSimplify("(A | C)", "(A | C) & (A | C | B | D)");
    assertSimplify("!A", "(!A) & ((!A) | B | D)");

    //  make sure it doesn't catch the opposite and/or case for whatever reason
    assertSimplify("(((A | C) & B & D) | (A & C))", "((A | C) & B & D) | (A & C)");
  }
}
