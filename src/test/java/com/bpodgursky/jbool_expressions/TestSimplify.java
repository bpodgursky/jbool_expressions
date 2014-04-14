package com.bpodgursky.jbool_expressions;

import java.util.ArrayList;

import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.SimplifyNExprChildren;
import com.google.common.collect.Lists;

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


    // test in isolation to catch a few potential errors
    ArrayList<Rule<?,String>> rules = Lists.<Rule<?, String>>newArrayList(new SimplifyNExprChildren<String>());

    assertApply("((A & B) & A)", "(A & B) & A", rules);
    assertApply("(A)", "A & (A | B)", rules);
    assertApply("(A)", "A | (A & B)", rules);
    assertApply("((A | B) | A)", "(A | B) | A", rules);

  }
}
