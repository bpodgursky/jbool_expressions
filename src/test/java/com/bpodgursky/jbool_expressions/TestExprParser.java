package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.rules.RuleSet;

public class TestExprParser extends JBoolTestCase {

  public void testIt(){
    assertEquals(Variable.of("A"), ExprParser.parse("A"));
    assertEquals(Variable.of("AA"), ExprParser.parse("AA"));
    assertEquals(Not.of(Variable.of("A")), ExprParser.parse("!A"));
    assertEquals(Not.of(Variable.of("A")), ExprParser.parse("   !  A "));
    assertEquals(Not.of(Variable.of("A")), ExprParser.parse("  ( !  (A) )"));
    assertEquals(And.of(Variable.of("A"), Variable.of("B")), ExprParser.parse("  A & (B)  "));
    assertEquals(And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C"))), RuleSet.simplify(ExprParser.parse("(  A & (B) & !C )")));
    assertEquals(Or.of(Variable.of("A"), Variable.of("B")), ExprParser.parse("(  A | (B)  )"));
    assertEquals(Not.of(Or.of(Variable.of("A"), Variable.of("B"))), ExprParser.parse("!(  A | (B)  )"));
    assertEquals(Or.of(And.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), ExprParser.parse("  A & (B) | C "));
    assertEquals(And.of(Or.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), ExprParser.parse("(A | B) & C"));
    assertEquals(And.of(Not.of(Or.of(Variable.of("A"), Variable.of("B"))), Variable.of("C")), ExprParser.parse("!(A | B) & C"));
    assertEquals(Or.of(Variable.of("A"), Variable.of("B"), Variable.of("C"), And.of(Variable.of("D"), Variable.of("E"))),
        RuleSet.simplify(ExprParser.parse("A | (B | C )| D&E")));

    assertEquals(Literal.getFalse(), ExprParser.parse("false"));
    assertEquals(Literal.getTrue(), ExprParser.parse("true"));
    assertEquals(Literal.getTrue(), ExprParser.parse("(true)"));

    assertEquals(Not.of(Literal.getTrue()), ExprParser.parse("!(true)"));
  }
}
