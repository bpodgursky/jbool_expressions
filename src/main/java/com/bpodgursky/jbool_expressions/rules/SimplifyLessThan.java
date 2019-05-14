package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.LessThan;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

public class SimplifyLessThan<K> extends Rule<LessThan<K>, K> {
    @Override
    public Expression<K> applyInternal(LessThan<K> input, ExprOptions<K> options) {
        return input;
    }

    @Override
    protected boolean isApply(Expression<K> input) {
        return input instanceof LessThan;
    }
}
