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

    private final Map<Expression<K>, Expression<K>> cache;
    private final Map<Class<? extends Rule>, Map<Expression<K>, Expression<K>>> simplificatonCache;

    public UnboundedCache() {
      this.simplificatonCache = new HashMap<>();
      this.cache = new HashMap<>();
    }

    @Override
    public Expression<K> get(Expression<K> input) {
      return cache.get(input);
    }

    @Override
    public Expression<K> get(Class<? extends Rule> rule, Expression<K> input) {

      Map<Expression<K>, Expression<K>> forRule = simplificatonCache.get(rule);

      if (forRule != null) {
        return forRule.get(input);
      }

      return null;

    }

    @Override
    public void put(Class<? extends Rule> rule, Expression<K> input, Expression<K> output) {

      if(!simplificatonCache.containsKey(rule)){
        simplificatonCache.put(rule, new HashMap<>());
      }

      simplificatonCache.get(rule).put(input, output);

    }

    @Override
    public void put(Expression<K> input, Expression<K> output) {

      if(cache.size() % 100000 == 0){
        System.out.println("cache size: "+cache.size());
      }

      cache.put(input, output);
    }

  }

  public static <K> Expression<K> applyAll(Expression<K> e, List<Rule<?, K>> rules) {
    return applyAll(e, rules, new UnboundedCache<>());
  }

  private static int hits = 0;
  private static int misses = 0;


  public static <K> Expression<K> applyAll(Expression<K> e, List<Rule<?, K>> rules, RuleSetCache<K> cache) {

    Expression<K> cached = cache.get(e);

    if((hits+misses) % 1000000 == 0){
      System.out.println();
      System.out.println("hits: "+hits);
      System.out.println("misses:" +misses);
    }

    if(cached != null){
      hits++;
      return cached;
    }else{
      misses++;
    }


    Expression<K> orig = e;
    Expression<K> simplified = applyAllSingle(orig, rules, cache);

    while (!orig.equals(simplified)) {

      if(orig.equals(simplified) && (orig != simplified)){
        System.out.println();
        System.out.println("y tho");
        System.out.println(orig);
        System.out.println(simplified);
      }

      orig = simplified;
      simplified = applyAllSingle(orig, rules, cache);
    }

    cache.put(e, simplified);

    return simplified;
  }

  private static <K> Expression<K> applyAllSingle(Expression<K> e, List<Rule<?, K>> rules, RuleSetCache<K> cache) {
    Expression<K> tmp = e.apply(rules, cache);

    for (Rule<?, K> r : rules) {
      Expression<K> input = tmp;
      Expression<K> cached = cache.get(r.getClass(), input);

      if (cached != null) {
        tmp = cached;
      }else{
        tmp = r.apply(tmp, cache);
        cache.put(r.getClass(), input, tmp);
      }

    }

    return tmp;
  }

  public static <K> Expression<K> applySet(Expression<K> root, List<Rule<?, K>> allRules) {
    return applyAll(root, allRules);
  }


}
