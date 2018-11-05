package com.bpodgursky.jbool_expressions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.QuineMcCluskey;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.rules.SimplifyNExprChildren;

public class TestSimplify extends JBoolTestCase {

  public void testSimplify() {

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
    assertSimplify("((A & C) | (B & D & (A | C)))", "((A | C) & B & D) | (A & C)");


    // test in isolation to catch a few potential errors
    ArrayList<Rule<?, String>> rules = new ArrayList<>(Collections.singletonList(new SimplifyNExprChildren<String>()));

    assertApply("(A & (A & B))", "(A & B) & A", rules);
    assertApply("(A)", "A & (A | B)", rules);
    assertApply("(A)", "A | (A & B)", rules);
    assertApply("(A | (A | B))", "(A | B) | A", rules);

    // test CollapseNegation rules
    assertSimplify("(A | C | D)", "A | (!A & C) | D");
    assertSimplify("(C | D | (A & E))", "(A & E) | (!(A & E) & C) | D");

  }

  public void testPOS() {
    assertToPos("(A & (!C | !D | !E | !F))", "A & (!A | !C | !D | !E | !F)");
    assertToSop("(D | !A)", "!((!D | !A) & A)");
    assertToSop("(A | !D)", "((!D & !A) | A)");
  }

  public void testCollapseNegation() {
    assertSimplify("(A | C | D)", "A | (!A & C) | D");
  }

  public void testExpandLarge() {

    //  I didn't actually check this by hand.  See https://github.com/bpodgursky/jbool_expressions/issues/13 for context.
    assertToSop("(m | n | (a & g & h & i & !l) | (a & g & h & j & !l) | (a & g & h & k & !l) | (b & g & h & i & !l) | (b & g & h & j & !l) | (b & g & h & k & !l) | (c & g & h & i & !l) | (c & g & h & j & !l) | (c & g & h & k & !l) | (d & g & h & i & !l) | (d & g & h & j & !l) | (d & g & h & k & !l) | (e & g & h & i & !l) | (e & g & h & j & !l) | (e & g & h & k & !l) | (f & g & h & i & !l) | (f & g & h & j & !l) | (f & g & h & k & !l))",
        "(( a | b | c | d | e | f ) & ( g & h & ( i | j | k ) & !( l | m | n ))) | ( m | n )"
    );

  }

  public void testQMCDNF() throws InterruptedException {

    AtomicBoolean finished = new AtomicBoolean(false);

    Thread thread = new Thread(() -> {
      RuleSet.toDNFViaQMC(Not.of(expr("((b4 & b1 & !b3 & !c1 & c2 & c5) | (b4 & b1 & !b3 & !c1 & c2 & !c5) | (b4 & b1 & !b3 & !c1 & !c2 & c5) | (b4 & b1 & !b3 & !c1 & !c2 & !c5) | (b1 & b3 & !c1 & c2 & c5) | (b1 & b3 & !c1 & c2 & !c5) | (b1 & b3 & !c1 & !c2 & c5) | (b1 & b3 & !c1 & !c2 & !c5) | (!b4 & b1 & !b3 & !c1 & c2 & c5) | (!b4 & b1 & !b3 & !c1 & c2 & !c5) | (!b4 & b1 & !b3 & !c1 & !c2 & c5) | (!b4 & b1 & !b3 & !c1 & !c2 & !c5) | (b4 & !b1 & !c1 & b2 & c2 & c5) | (b4 & !b1 & !c1 & b2 & c2 & !c5) | (b4 & !b1 & !c1 & b2 & !c2 & c5) | (b4 & !b1 & !c1 & b2 & !c2 & !c5) | (!b1 & !c1 & !b2 & c2 & c5) | (!b1 & !c1 & !b2 & c2 & !c5) | (!b1 & !c1 & !b2 & !c2 & c5) | (!b1 & !c1 & !b2 & !c2 & !c5) | (!b4 & !b1 & !c1 & b2 & c2 & c5) | (!b4 & !b1 & !c1 & b2 & c2 & !c5) | (!b4 & !b1 & !c1 & b2 & !c2 & c5) | (!b4 & !b1 & !c1 & b2 & !c2 & !c5))")), ExprOptions.allCaching());
      finished.set(true);
    });

    thread.start();

    Thread.sleep(1000);

    if(!finished.get()){
      fail("QMC is not as fast as expected on this expression.");
    }

  }

  //expr("!(!(A1|A2|A3)&!(B1&B2)&!(B3&B4)&!B5&(C1|C2|C3|C4))&!(!(A1|A2|A3)&!(B1&B2)&!(B5|B4)&!D1&C1)&!(!(A1|A2|A3)&!(B1&B2)&!D1&!B5&C1)&!(!(A1|A2|A3)&!(B6|B5)&!D1&(B7|B8)&(C1|C2|C4)&B9)&!(!(A1|A2|A3)&!(B5|B4)&!D1&(C1|C2|C4)&B8&B3)&!(!(A1|A2|A3)&!D1&!B1&(C1|C2|C4)&B2)&!(!(A1|A2|A3)&!D1&!B6&(C1|C2|C4)&B10)&!(!(A1|A2|A3)&B1&B2&C3)&!(!(B1&B2)&!(B3&B5)&(A1|A2)&(C1|C2))&!(!(B1&B2)&!(B3&B5)&(A1|A2)&772&C1)&!(!(B1&B2)&!(B3&B5)&(A1|A2)&C1)&!(!(B1&B2)&!(B3&B5)&C3)&!(!(B6|B5)&!D1&(A1|A2)&(C1|C2)&017&B8&B9)&!(!D1&!A3&(C1|C2|C4)&B7&B3)&!(!E1&B1&B2&C3)&!((!(B11&M014)&!(B11&B10)&!(B3&B4)&!B1&F1&C3)|(!(B11&B10)&!(B3&B4)&!B2&F1&C3))&!((!(B3|B9)&!B6&(C1|C2|C4)&D1)|(!B6&!B5&(C1|C2|C4)&D1))&!((F1|5XXL)&B1&B2&C3)&!((A1|A2|A3)&B1&B2&C3)&!((A1|A2)&B1&B2&C3)&!((B3|B9)&(C1|C2|C3|C4)&017&B5)&!(A3&C1)&!(B1&B2&C3)&(A1|A2|A4|A5|A6|A7|A8|A9|A10|A11|A12|A13)")

  //  IDEAS

  //  count sub-expressions
  //  is the goal to eliminate size == make normal simplification faster
  //    swap out big expressions
  //  goal is to eliminate variables == make qmc faster
  //    first level -- see if swapping out any single common subexpression eliminates any variables
  //    second -- see if swapping out any two, three, X subexpressions eliminates any variables
  //      challenge -- find ones which are likely to contain all of some variables.  this is min cover problem.
  //  get all sets of expressions that appear together as children


}
