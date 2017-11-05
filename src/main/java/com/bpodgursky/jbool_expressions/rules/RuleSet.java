package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;
import com.google.common.collect.Lists;

//  intended user facing methods
public class RuleSet {

  public static <K> Expression<K> simplify(Expression<K> root) {
    return RulesHelper.applySet(root, RulesHelper.<K>simplifyRules());
  }

  public static <K> Expression<K> toSop(Expression<K> root){
    root = RulesHelper.applySet(root, Lists.<Rule<?, K>>newArrayList(new DeMorgan<K>()));
    return RulesHelper.applySet(root, RulesHelper.<K>toSopRules());
  }

  public static <K> Expression<K> toPos(Expression<K> root) {

    //   not + simplify
    Not<K> inverse = Not.of(root);
    Expression<K> sopInv = toSop(inverse);

    //  not + demorgan
    Not<K> inverse2 = Not.of(sopInv);

    return (RulesHelper.applySet(inverse2, RulesHelper.<K>demorganRules()));
  }

  /**
   * More formal name for sum-of-products
   */
  public static <K> Expression<K> toDNF(Expression<K> root) {
    return toSop(root);
  }

  /**
   * More formal name for product-of-sums
   */
  public static <K> Expression<K> toCNF(Expression<K> root) {
    return toPos(root);
  }

}
