package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.NExpression;

public abstract class SimplifyNExpression<K> extends Rule<NExpression<K>, K> {

  @Override
  public Expression<K> applyInternal(NExpression<K> input) {

    if(isMatch(input)){

      //  remove duplicates (already sorted)
      Expression<K>[] subCopy = ExprUtil.expr(input.expressions.length);

      int i = 1;
      int j = 1;

      subCopy[0] = input.expressions[0];
      while(i < input.expressions.length){
        Expression<K> internal = input.expressions[i++];

        if(!internal.equals(subCopy[j-1])){
          subCopy[j++] = internal;
        }
      }

      if(j == 1){
        return subCopy[0];
      }

      Expression<K>[] retain = ExprUtil.expr(j);
      System.arraycopy(subCopy, 0, retain, 0, j);

      return simplifyInternal(retain);
    }

    return input;
  }

  protected abstract boolean isMatch(Expression<K> e);

  protected abstract Expression<K> simplifyInternal(Expression<K>[] retain);
}
