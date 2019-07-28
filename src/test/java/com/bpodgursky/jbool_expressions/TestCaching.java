package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.options.ExprOptions;

public class TestCaching extends JBoolTestCase {

  public void testWithCaching() {
    assertToSop("((A & C) | (A & D))", "( A & ( C | D))", ExprOptions.allCacheIntern());
    assertToSop("((A & C) | (A & D) | (B & C) | (B & D))", "( ( A|  B) & ( C|  D))", ExprOptions.allCacheIntern());
  }

}
