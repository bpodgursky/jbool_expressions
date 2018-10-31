package com.bpodgursky.jbool_expressions.rules;

import javax.xml.stream.FactoryConfigurationError;

import com.bpodgursky.jbool_expressions.*;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.*;

public class SimplifyOr<K> extends Rule<Or<K>, K> {

  @Override
  public Expression<K> applyInternal(Or<K> input, RuleSetCache<K> cache) {

    for (Expression<K> expr : input.expressions) {
      if (expr instanceof Literal) {
        Literal l = (Literal) expr;

        //  ignore anything that is "false"
        if (!l.getValue()) {
          return copyWithoutFalse(input, cache.factory());
        } else {
          return Literal.of(true);
        }
      }

      //  succeed immediately if require something or its opposite
      if( expr instanceof Not){
        Expression<K> notChild = ((Not<K>)expr).getE();
        for(Expression<K> child: input.expressions){
          if(child.equals(notChild)){
            return Literal.getTrue();
          }
        }
      }
    }

    return input;
  }

  private Expression<K> copyWithoutFalse(Or<K> input, ExprFactory<K> factory){
    List<Expression<K>> copy = new ArrayList<>();
    for (Expression<K> expr : input.expressions) {
      if (expr instanceof Literal) {
        Literal l = (Literal) expr;

        //  ignore anything that is "false"
        if (!l.getValue()) {
          continue;
        }
      }
      copy.add(expr);
    }

    if (copy.isEmpty()) {
      return Literal.of(false);
    }

    return factory.or(copy.toArray(new Expression[copy.size()]));
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof Or;
  }
}
