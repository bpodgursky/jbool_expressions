package jbool_expressions;

import junit.framework.TestCase;

import java.util.Arrays;

public class TestPrefixParser extends TestCase {

  public void testIt(){

    //  tokenize
    assertEquals(Arrays.asList("+", "A", "B", "C"), PrefixParser.tokenize("+ A B C"));
    assertEquals(Arrays.asList("+", "A", "B", "C"), PrefixParser.tokenize(" +  A   B C  "));
    assertEquals(Arrays.asList("+", "(A   B)", "C"), PrefixParser.tokenize(" +  (A   B) C  "));
    assertEquals(Arrays.asList("( +)", "(A   B)", "C"), PrefixParser.tokenize("( +)  (A   B) C  "));

    // parse
    assertEquals(Variable.of("A"), PrefixParser.parse("A"));
    assertEquals(Variable.of("AB"), PrefixParser.parse("AB"));
    assertEquals(Not.of(Variable.of("AB")), PrefixParser.parse("(! AB)"));
    assertEquals(Not.of(Not.of(Variable.of("AB"))), PrefixParser.parse("(!   (! AB))"));
    assertEquals(And.of(Variable.of("A"), Variable.of("B")), PrefixParser.parse("(* A B)"));
    assertEquals(Or.of(Variable.of("A"), Variable.of("B")), PrefixParser.parse("(+ A B)"));
    assertEquals(Or.of(And.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), PrefixParser.parse("(+  (* A B)  C)"));
    assertEquals(Or.of(And.of(Variable.of("A"), Not.of(Variable.of("B"))), Variable.of("C")), PrefixParser.parse("(+  (* A (! B))  C)"));
  }
}
