package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bpodgursky.jbool_expressions.Expression;

public class RulesHelper {

  public static <K> List<Rule<?, K>> simplifyRules() {
    List<Rule<?, K>> rules = new ArrayList<>();
    rules.add(new SimplifyAnd<K>());
    rules.add(new SimplifyOr<K>());
    rules.add(new SimplifyNot<K>());
    rules.add(new CombineAnd<K>());
    rules.add(new CombineOr<K>());
    rules.add(new SimplifyNExpression<K>());
    rules.add(new SimplifyNExprChildren<K>());
    rules.add(new CollapseNegation<K>());

    return rules;
  }

  public static <K> List<Rule<?, K>> toSopRules() {
    List<Rule<?, K>> rules = new ArrayList<>(RulesHelper.<K>simplifyRules());
    rules.add(new ToSOP<K>());

    return rules;
  }

  public static <K> List<Rule<?, K>> demorganRules() {
    List<Rule<?, K>> rules = new ArrayList<>(RulesHelper.<K>simplifyRules());
    rules.add(new DeMorgan<K>());

    return rules;
  }

  public static class UnboundedCache<K> implements RuleSetCache<K> {

    private final Map<Expression<K>, Expression<K>> simplificatonCache;

    private final Map<Expression<K>, Expression<K>> interned;


    public UnboundedCache() {
      this.simplificatonCache = new HashMap<>();
      this.interned = new HashMap<>();
    }

    @Override
    public Expression<K> get(Expression<K> input) {
      return simplificatonCache.get(input);
    }

    @Override
    public void put(Expression<K> input, Expression<K> output) {
      simplificatonCache.put(input, output);

      if(simplificatonCache.size() % 100000 == 0){
        System.out.println(simplificatonCache.size());
      }

    }

//    @Override
//    public Expression<K> intern(Expression<K> input) {
//      if(interned.containsKey(input)){
//        return interned.get(input);
//      }
//      interned.put(input, input);
//      return input;
//    }


  }

  public static <K> Expression<K> applyAll(Expression<K> e, List<Rule<?, K>> rules) {
    return applyAll(e, rules, new UnboundedCache<>());
  }

  private static int hits = 0;
  private static int misses = 0;


  public static <K> Expression<K> applyAll(Expression<K> e, List<Rule<?, K>> rules, RuleSetCache<K> cache) {

    Expression<K> cached = cache.get(e);
    if(cached != null){
      hits++;
      return cached;
    }else{
      misses++;
    }

    if((hits+misses) % 1000000 == 0){
      System.out.println();
      System.out.println(hits);
      System.out.println(misses);
    }

    Expression<K> orig = e;
    Expression<K> simplified = applyAllSingle(orig, rules, cache);

    while (!orig.equals(simplified)) {
      orig = simplified;
      simplified = applyAllSingle(orig, rules, cache);
    }

    cache.put(e, simplified);

    return simplified;
  }

  private static <K> Expression<K> applyAllSingle(Expression<K> e, List<Rule<?, K>> rules, RuleSetCache<K> cache) {
    Expression<K> tmp = e.apply(rules, cache);
    for (Rule<?, K> r : rules) {
      tmp = r.apply(tmp, cache);
    }
    return tmp;
  }

  public static <K> Expression<K> applySet(Expression<K> root, List<Rule<?, K>> allRules) {
    return applyAll(root, allRules);
  }


}
