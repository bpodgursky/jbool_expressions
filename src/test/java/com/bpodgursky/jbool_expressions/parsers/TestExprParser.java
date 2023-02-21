package com.bpodgursky.jbool_expressions.parsers;

import com.bpodgursky.jbool_expressions.*;
import org.junit.jupiter.api.Test;

public class TestExprParser extends JBoolTestCase {

  @Test
  public void testIt() {
    assertLexEquals(Variable.of("A"), ExprParser.parse("A"));
    assertLexEquals(Variable.of("AA"), ExprParser.parse("AA"));
    assertLexEquals(Not.of(Variable.of("A")), ExprParser.parse("!A"));
    assertLexEquals(Not.of(Variable.of("A")), ExprParser.parse("   !  A "));
    assertLexEquals(Not.of(Variable.of("A")), ExprParser.parse("  ( !  (A) )"));
    assertLexEquals(And.of(Variable.of("A"), Variable.of("B")), ExprParser.parse("  A & (B)  "));
    assertLexEquals(And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C"))), ExprParser.parse("(  A & (B) & !C )"));
    assertLexEquals(Or.of(Variable.of("A"), Variable.of("B")), ExprParser.parse("(  A | (B)  )"));
    assertLexEquals(Not.of(Or.of(Variable.of("A"), Variable.of("B"))), ExprParser.parse("!(  A | (B)  )"));
    assertLexEquals(Or.of(And.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), ExprParser.parse("  A & (B) | C "));
    assertLexEquals(And.of(Or.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), ExprParser.parse("(A | B) & C"));
    assertLexEquals(And.of(Not.of(Or.of(Variable.of("A"), Variable.of("B"))), Variable.of("C")), ExprParser.parse("!(A | B) & C"));
    assertLexEquals(Or.of(Variable.of("A"), Or.of(Variable.of("B"), Variable.of("C")), And.of(Variable.of("D"), Variable.of("E"))),
        ExprParser.parse("A | (B | C )| D & E"));

    assertLexEquals(Or.of(Variable.of("A"), Variable.of("B"), Variable.of("C"), And.of(Variable.of("D"), Variable.of("E"))),
            ExprParser.parse("A | B | C | D & E"));

    assertLexEquals(Literal.getFalse(), ExprParser.parse("false"));
    assertLexEquals(Literal.getTrue(), ExprParser.parse("true"));
    assertLexEquals(Literal.getTrue(), ExprParser.parse("(true)"));

    assertLexEquals(Not.of(Literal.getTrue()), ExprParser.parse("!(true)"));

    assertLexEquals(And.of(Not.of(Variable.of("' A:aa+)(*&^%$#@!_123'")), Variable.of("A")), ExprParser.parse("!' A:aa+)(*&^%$#@!_123' & A"));

    assertLexEquals(
        Not.of(Not.of(Variable.of("A"))),
        ExprParser.parse("!!A")
    );

    assertLexEquals(
        Not.of(Not.of(Not.of(Variable.of("A")))),
        ExprParser.parse("!!!A")
    );

    assertLexEquals(
        Not.of(Not.of(Or.of(Variable.of("A"), Variable.of("B")))),
        ExprParser.parse("!!(A | B)")
    );

    assertLexEquals(Or.of(Variable.of("A"), Or.of(Variable.of("B"), Variable.of("C"))), ExprParser.parse("(A|(B|C))"));
    assertLexEquals(Or.of(Variable.of("A"), Variable.of("B"), Variable.of("C")  ), ExprParser.parse("(A|B|C)"));
  }

  @Test
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

  @Test
  public void testSingleQuotes(){
    assertEquals(Or.of(Variable.of("'test@gmail.com'"), Variable.of("'test2@gmail.com'")),
        ExprParser.parse("( 'test@gmail.com' | 'test2@gmail.com' )"));

    assertEquals(Or.of(Variable.of("'test & gmail.com'"), Variable.of("'test2 & gmail.com'")),
        ExprParser.parse("( 'test & gmail.com' | 'test2 & gmail.com' )"));
  }

  @Test
  public void testDoubleQuotes(){
    assertEquals(Or.of(Variable.of("\"test@gmail.com\""), Variable.of("\"test2@gmail.com\"")),
        ExprParser.parse("( \"test@gmail.com\" | \"test2@gmail.com\" )"));

    //  defend against the indefensible
    assertEquals(Or.of(Variable.of("\"test & gmail.com\""), Variable.of("\"test2 & gmail.com\"")),
        ExprParser.parse("( \"test & gmail.com\" | \"test2 & gmail.com\" )"));
  }

  @Test
  public void testLexSort() {
    assertEquals("(A & B & !C)", And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C")))
        .toString()
    );

    assertEquals("(!C & A & B)", And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C")))
        .toLexicographicString()
    );
  }
}
