package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.GreaterThan;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

public class SimplifyGreaterThan<K> extends Rule<GreaterThan<K>, K> {
    @Override
    public Expression<K> applyInternal(GreaterThan<K> input, ExprOptions<K> options) {
        return input;
    }

    @Override
    protected boolean isApply(Expression<K> input) {
        return input instanceof GreaterThan;
    }
}
