package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RulesHelper;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.*;
import java.util.function.Function;

public class LessThan<K> extends Expression<K> {

    public static final String EXPR_TYPE = "less than";
    private String cachedStringRepresentation = null;

    private final Expression<K> e;

    public LessThan(Expression<K> e) {
        this.e = e;
    }


    public static <K> LessThan<K> of(Expression<K> e) {
        return new LessThan<K>(e);
    }

    public String toString() {
        if (cachedStringRepresentation == null) {
            cachedStringRepresentation = "<" + e;
        }
        return cachedStringRepresentation;
    }

    public Expression<K> getE() {
        return e;
    }

    @Override
    public Expression<K> apply(List<Rule<?, K>> rules, ExprOptions<K> options) {
        Expression<K> e = RulesHelper.applyAll(this.e, rules, options);

        if(e != this.e){
            return options.getExprFactory().lt(e);
        }

        return this;
    }

    @Override
    public List<Expression<K>> getChildren() {
        return Collections.singletonList(e);
    }

    @Override
    public Expression<K> map(Function<Expression<K>, Expression<K>> function, ExprFactory<K> factory) {
        Expression<K> map = this.e.map(function, factory);

        if(map != this.e){
            return function.apply(factory.gt(map));
        }

        return function.apply(this);
    }

    @Override
    public String getExprType() {
        return EXPR_TYPE;
    }

    @Override
    public Expression<K> sort(Comparator<Expression> comparator) {
        return LessThan.of(e.sort(comparator));
    }

    @Override
    public void collectK(Set<K> set, int limit) {
        e.collectK(set, limit);
    }

    @Override
    public Expression<K> replaceVars(Map<K, Expression<K>> m, ExprFactory<K> factory) {
        return of(e.replaceVars(m, factory));
    }


}
