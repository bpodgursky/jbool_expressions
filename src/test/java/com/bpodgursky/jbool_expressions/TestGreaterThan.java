package com.bpodgursky.jbool_expressions;

public class TestGreaterThan extends JBoolTestCase {

    public void testSimplify() {
        assertSimplify("<E", "(< E)");
        assertSimplify("<E", "(!(!(< E)))");
        assertSimplify("(E | <E)", "(!(!(E | < E)))");
    }
}
