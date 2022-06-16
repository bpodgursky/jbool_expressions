package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.NExpression;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The rule applies <a href="https://en.wikipedia.org/wiki/Distributive_property">Distributive property</a>
 */
public class DistributiveLaw<K> extends Rule<NExpression<K>, K> {

    @Override
    public Expression<K> applyInternal(NExpression<K> input, ExprOptions<K> options) {
        final ExprFactory<K> factory = options.getExprFactory();
        if (input instanceof And) {
            return doApplyInternal(input, factory::or, factory::and);
        } else if (input instanceof Or) {
            return doApplyInternal(input, factory::and, factory::or);
        }
        throw new IllegalArgumentException("Not supported input expression:" + input);
    }

    @SuppressWarnings("unchecked")
    private Expression<K> doApplyInternal(NExpression<K> input, ExpressionCreator<K> upperExpressionCreator, ExpressionCreator<K> innerExpressionCreator) {
        final Expression<K> commonExpression = findCommonExpression(input.getChildren())
                .orElseThrow(() -> new IllegalArgumentException("Common expression is not found in " + input));
        return upperExpressionCreator.apply(
                new Expression[]{
                        commonExpression,
                        innerExpressionCreator.apply(input.getChildren()
                                .stream()
                                .map(child -> excludeChild(child, upperExpressionCreator, commonExpression))
                                .toArray(Expression[]::new)
                        )
                }
        );
    }

    @SuppressWarnings("unchecked")
    private Expression<K> excludeChild(Expression<K> expression, ExpressionCreator<K> newExpressionCreator, Expression<K> childToExclude) {
        final Expression<K>[] leftChildren = expression.getChildren()
                .stream()
                .filter(child -> !childToExclude.equals(child))
                .toArray(Expression[]::new);
        if (leftChildren.length == 1) {
            return leftChildren[0];
        } else {
            return newExpressionCreator.apply(leftChildren);
        }
    }

    @Override
    protected boolean isApply(Expression<K> input) {
        if (input == null) {
            return false;
        }

        if (input instanceof And) {
            return isApplicable(input, Or.class);
        } else if (input instanceof Or) {
            return isApplicable(input, And.class);
        }
        return false;
    }

    private <C extends NExpression<?>> boolean isApplicable(Expression<K> input, Class<C> childClass) {
        final List<Expression<K>> children = input.getChildren();
        if (children == null || children.size() < 2) {
            return false;
        }
        if (children.stream().anyMatch(child -> !childClass.isInstance(child))) {
            return false;
        }
        return findCommonExpression(children).isPresent();
    }

    private Optional<Expression<K>> findCommonExpression(List<Expression<K>> expressions) {
        return expressions.stream()
                .skip(1)
                .reduce(
                        expressions.get(0).getChildren(),
                        (commons, expression) -> commons.stream().filter(element -> expression.getChildren().stream().anyMatch(element::equals)).collect(Collectors.toList()),
                        (list1, list2) -> list1.stream().filter(element1 -> list1.stream().anyMatch(element1::equals)).collect(Collectors.toList()))
                .stream()
                .findFirst();
    }

    @FunctionalInterface
    private interface ExpressionCreator<K> extends Function<Expression<K>[], Expression<K>> {
    }
}
