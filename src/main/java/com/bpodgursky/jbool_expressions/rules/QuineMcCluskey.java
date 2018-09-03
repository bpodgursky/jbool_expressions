package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.eval.EvalEngine;

public class QuineMcCluskey {

  public <K> Expression<K> simplify(Expression<K> input) {

    ArrayList<K> variables = new ArrayList<>(input.getAllK());
    boolean[] assignments = new boolean[variables.size()];

    //  expand all true/false inputs


  }

  <K> List<Integer> findMinterms(int pos, ArrayList<K> variables, boolean[] assignments, Expression<K> input){

    if (pos == variables.size()){


      EvalEngine.evaluate(input, )

      //  evaluate

    }

    assignments[pos] = true;
    List<Integer> minterms = new ArrayList<>(findMinterms(pos + 1, variables, assignments, input));

    assignments[pos] = false;
    minterms.addAll(findMinterms(pos+1, variables, assignments, input));

    return minterms;
  }

}
