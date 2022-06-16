package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.JBoolTestCase;

import java.util.Collections;

public class TestDistributiveLaw extends JBoolTestCase {

    public void testSimplify() {
        RuleList<String> rules = new RuleList<>(Collections.singletonList(new DistributiveLaw<>()));

        // simple cases
        assertApply("(A | (B & C))", "(A | B) & (A | C)", rules);
        assertApply("(A & (B | C))", "(A & B) | (A & C)", rules);

        // more than 2 elements in expression
        assertApply("(B & ((A & C) | (C & D) | (D & E)))", "(A & B & C) | (B & C & D) | (B & D & E)", rules);

        // more than 2 common elements
        assertApply("(B & (C & (A | D | E)))", "(A & B & C) | (B & C & D) | (B & C & E)", rules);
    }
}
