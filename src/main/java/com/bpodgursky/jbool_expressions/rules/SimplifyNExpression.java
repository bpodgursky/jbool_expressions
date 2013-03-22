package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.*;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class SimplifyNExpression<K> extends Rule<NExpression<K>, K> {

  @Override
  public Expression<K> applyInternal(NExpression<K> input) {

    if(input.expressions.length == 1){
      return input.expressions[0];
    }

    for(int i = 0; i < input.expressions.length-1; i++){
      if(input.expressions[i].equals(input.expressions[i+1])){
        return simplify(input);
      }
    }

    return input;
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof NExpression;
  }

  private Expression<K> simplify(NExpression<K> input){
    //  remove duplicates (already sorted)

    int i = 1;
    int j = 1;

    ArrayList<Expression<K>> copy = Lists.newArrayList();
    copy.add(input.expressions[0]);

    while(i < input.expressions.length){
      Expression<K> internal = input.expressions[i++];

      if(!internal.equals(copy.get(j-1))){
        copy.add(internal);
        j++;
      }
    }

    return build(copy, input);
  }

  private NExpression<K> build(List<Expression<K>> contents, NExpression<K> input){
    if(input instanceof And){
      return And.of(contents);
    }else{
      return Or.of(contents);
    }
  }
}
