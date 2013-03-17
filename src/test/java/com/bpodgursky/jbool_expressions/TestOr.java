package com.bpodgursky.jbool_expressions;

public class TestOr extends JBoolTestCase {

  public void testSimplify(){
    assertSimplify("A", "(+ A A)");
    assertSimplify("A", "(+ A A A)");
    assertSimplify("true", "(+ A A (! A))");
    assertSimplify("true", "(+ A A true)");
    assertSimplify("A", "(+ A A false)");

  }

  public void testAndTrue(){
    assertSimplify("true", "(+ true (! (! true)))");
    assertSimplify("true", "(+ true (! (! false)))");
  }

  public void testTwoVar(){
    assertSimplify("(+ A B)", "(+ A B A)");
  }

  public void testNExpr(){
    assertSimplify("true", "(+ A B A (* true (! (! true))))");
  }
}
