package com.bpodgursky.jbool_expressions.parsers;

import com.bpodgursky.jbool_expressions.*;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import junit.framework.Assert;

public class TestExprParser extends JBoolTestCase {

  public void testIt() {
    Assert.assertEquals(Variable.of("A"), ExprParser.parse("A"));
    Assert.assertEquals(Variable.of("AA"), ExprParser.parse("AA"));
    Assert.assertEquals(Not.of(Variable.of("A")), ExprParser.parse("!A"));
    Assert.assertEquals(Not.of(Variable.of("A")), ExprParser.parse("   !  A "));
    Assert.assertEquals(Not.of(Variable.of("A")), ExprParser.parse("  ( !  (A) )"));
    Assert.assertEquals(And.of(Variable.of("A"), Variable.of("B")), ExprParser.parse("  A & (B)  "));
    Assert.assertEquals(And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C"))), RuleSet.simplify(ExprParser.parse("(  A & (B) & !C )")));
    Assert.assertEquals(Or.of(Variable.of("A"), Variable.of("B")), ExprParser.parse("(  A | (B)  )"));
    Assert.assertEquals(Not.of(Or.of(Variable.of("A"), Variable.of("B"))), ExprParser.parse("!(  A | (B)  )"));
    Assert.assertEquals(Or.of(And.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), ExprParser.parse("  A & (B) | C "));
    Assert.assertEquals(And.of(Or.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), ExprParser.parse("(A | B) & C"));
    Assert.assertEquals(And.of(Not.of(Or.of(Variable.of("A"), Variable.of("B"))), Variable.of("C")), ExprParser.parse("!(A | B) & C"));
    Assert.assertEquals(Or.of(Variable.of("A"), Variable.of("B"), Variable.of("C"), And.of(Variable.of("D"), Variable.of("E"))),
        RuleSet.simplify(ExprParser.parse("A | (B | C )| D&E")));

    Assert.assertEquals(Literal.<String>getFalse(), ExprParser.parse("false"));
    Assert.assertEquals(Literal.<String>getTrue(), ExprParser.parse("true"));
    Assert.assertEquals(Literal.<String>getTrue(), ExprParser.parse("(true)"));

    Assert.assertEquals(Not.of(Literal.<String>getTrue()), ExprParser.parse("!(true)"));

    Assert.assertEquals(And.of(Not.of(Variable.of("' A:aa+)(*&^%$#@!_123'")), Variable.of("A")), ExprParser.parse("!' A:aa+)(*&^%$#@!_123' & A"));

    Assert.assertEquals(
        Not.of(Not.of(Variable.of("A"))),
        ExprParser.parse("!!A")
    );

    Assert.assertEquals(
        Not.of(Not.of(Not.of(Variable.of("A")))),
        ExprParser.parse("!!!A")
    );

    Assert.assertEquals(
        Not.of(Not.of(Or.of(Variable.of("A"), Variable.of("B")))),
        ExprParser.parse("!!(A | B)")
    );


  }

  public void testQuotedMapper() {

    QuotedMapper<Integer> intMapper = new QuotedMapper<Integer>() {
      @Override
      public Integer getValue(String name) {
        return Integer.parseInt(name);
      }
    };

    assertEquals(Variable.of(1), ExprParser.parse("'1'", intMapper));
    assertEquals(Not.of(Variable.of(1)), ExprParser.parse("!'1'", intMapper));
  }

  public void testLexSort() {

    assertEquals("(A & B & !C)", And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C")))
        .toString()
    );

    assertEquals("(!C & A & B)", And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C")))
        .toLexicographicString()
    );

  }
}
