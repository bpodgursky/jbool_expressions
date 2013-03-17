package com.bpodgursky.jbool_expressions.rules;

import com.google.common.collect.Lists;
import com.bpodgursky.jbool_expressions.Expression;

import java.util.*;

public class RuleSet {

  public static <K> List<Rule<?, K>> simplifyRules(){
    List<Rule<?, K>> rules = Lists.newArrayList();
    rules.add(new SimplifyAnd<K>());
    rules.add(new SimplifyOr<K>());
    rules.add(new SimplifyNot<K>());
    rules.add(new CombineAnd<K>());
    rules.add(new CombineOr<K>());

    return rules;
  }

  public static <K> List<Rule<?, K>> toSopRules(){
    List<Rule<?, K>> rules = Lists.newArrayList(RuleSet.<K>simplifyRules());
    rules.add(new ToSOP<K>());
    rules.add(new DeMorgan<K>());

    return rules;
  }


  public static <K> Expression<K> applyAll(Expression<K> e, List<Rule<?, K>> rules){
    Expression<K> orig = e;
    Expression<K> simplified = applyAllSingle(orig, rules);

    while(!orig.equals(simplified)){
      orig = simplified;
      simplified = applyAllSingle(orig, rules);
    }

    return simplified;
  }

  private static <K> Expression<K> applyAllSingle(Expression<K> e, List<Rule<?, K>> rules){
    Expression<K> tmp = e.apply(rules);
    for(Rule<?, K> r: rules){
      tmp = r.apply(tmp);
    }
    return tmp;
  }

  public static <K> Expression<K> simplify(Expression<K> root){
    return applySet(root, RuleSet.<K>simplifyRules());
  }

  public static <K> Expression<K> toSop(Expression<K> root){
    return applySet(root, RuleSet.<K>toSopRules());
  }

  public static <K> Expression<K> assign(Expression<K> root, Map<K, Boolean> values){
    List<Rule<?, K>> rules = Lists.newArrayList(RuleSet.<K>simplifyRules());
    rules.add(new Assign<K>(values));
    return applySet(root, rules);
  }

  public static <K> Expression<K> applySet(Expression<K> root, List<Rule<?, K>> allRules){
    return applyAll(root, allRules);
  }
}
