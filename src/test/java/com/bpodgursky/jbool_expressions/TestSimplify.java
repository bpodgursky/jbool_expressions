package com.bpodgursky.jbool_expressions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.RuleList;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.jbool_expressions.rules.SimplifyNExprChildren;
import org.junit.jupiter.api.Test;

public class TestSimplify extends JBoolTestCase {

  @Test
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
    RuleList<String> rules = new RuleList<>(new ArrayList<>(Collections.singletonList(new SimplifyNExprChildren<>())));

    assertApply("(A & (A & B))", "(A & B) & A", rules);
    assertApply("(A)", "A & (A | B)", rules);
    assertApply("(A)", "A | (A & B)", rules);
    assertApply("(A | (A | B))", "(A | B) | A", rules);

    // test CollapseNegation rules
    assertSimplify("(A | C | D)", "A | (!A & C) | D");
    assertSimplify("(C | D | (A & E))", "(A & E) | (!(A & E) & C) | D");
  }

  @Test
  public void testPOS() {
    assertToPos("(A & (!C | !D | !E | !F))", "A & (!A | !C | !D | !E | !F)");
    assertToSop("(D | !A)", "!((!D | !A) & A)");
    assertToSop("(A | !D)", "((!D & !A) | A)");
  }

  @Test
  public void testCollapseNegation() {
    assertSimplify("(A | C | D)", "A | (!A & C) | D");
  }

  @Test
  public void testExpandLarge() {
    //  I didn't actually check this by hand.  See https://github.com/bpodgursky/jbool_expressions/issues/13 for context.
    assertToSop("(m | n | (a & g & h & i & !l) | (a & g & h & j & !l) | (a & g & h & k & !l) | (b & g & h & i & !l) | (b & g & h & j & !l) | (b & g & h & k & !l) | (c & g & h & i & !l) | (c & g & h & j & !l) | (c & g & h & k & !l) | (d & g & h & i & !l) | (d & g & h & j & !l) | (d & g & h & k & !l) | (e & g & h & i & !l) | (e & g & h & j & !l) | (e & g & h & k & !l) | (f & g & h & i & !l) | (f & g & h & j & !l) | (f & g & h & k & !l))",
        "(( a | b | c | d | e | f ) & ( g & h & ( i | j | k ) & !( l | m | n ))) | ( m | n )"
    );
  }

  @Test
  public void testQMCDNF() throws InterruptedException {
    AtomicBoolean finished = new AtomicBoolean(false);

    Thread thread = new Thread(() -> {
      RuleSet.toDNFViaQMC(Not.of(expr("((b4 & b1 & !b3 & !c1 & c2 & c5) | (b4 & b1 & !b3 & !c1 & c2 & !c5) | (b4 & b1 & !b3 & !c1 & !c2 & c5) | (b4 & b1 & !b3 & !c1 & !c2 & !c5) | (b1 & b3 & !c1 & c2 & c5) | (b1 & b3 & !c1 & c2 & !c5) | (b1 & b3 & !c1 & !c2 & c5) | (b1 & b3 & !c1 & !c2 & !c5) | (!b4 & b1 & !b3 & !c1 & c2 & c5) | (!b4 & b1 & !b3 & !c1 & c2 & !c5) | (!b4 & b1 & !b3 & !c1 & !c2 & c5) | (!b4 & b1 & !b3 & !c1 & !c2 & !c5) | (b4 & !b1 & !c1 & b2 & c2 & c5) | (b4 & !b1 & !c1 & b2 & c2 & !c5) | (b4 & !b1 & !c1 & b2 & !c2 & c5) | (b4 & !b1 & !c1 & b2 & !c2 & !c5) | (!b1 & !c1 & !b2 & c2 & c5) | (!b1 & !c1 & !b2 & c2 & !c5) | (!b1 & !c1 & !b2 & !c2 & c5) | (!b1 & !c1 & !b2 & !c2 & !c5) | (!b4 & !b1 & !c1 & b2 & c2 & c5) | (!b4 & !b1 & !c1 & b2 & c2 & !c5) | (!b4 & !b1 & !c1 & b2 & !c2 & c5) | (!b4 & !b1 & !c1 & b2 & !c2 & !c5))")), ExprOptions.allCacheIntern());
      finished.set(true);
    });

    thread.start();

    Thread.sleep(1000);

    if(!finished.get()){
      fail("QMC is not as fast as expected on this expression.");
    }
  }
}
