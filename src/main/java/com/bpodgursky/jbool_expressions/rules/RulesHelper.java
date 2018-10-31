package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

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
    private final ExprFactory<K> factory;

    private final Map<Expression<K>, Expression<K>> intern = new HashMap<>();

    public UnboundedCache(ExprFactory<K> factory) {
      this.simplificatonCache = new HashMap<>();
      this.cache = new HashMap<>();
      this.factory = factory;
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

      Expression<K> shrunkInput = input.map(new RuleSet.Intern<>(intern), factory);
      Expression<K> shrunkOutput = output.map(new RuleSet.Intern<>(intern), factory);

      if(!simplificatonCache.containsKey(rule)){
        simplificatonCache.put(rule, new HashMap<>());
      }

      Map<Expression<K>, Expression<K>> ruleCache = simplificatonCache.get(rule);
      ruleCache.put(shrunkInput, shrunkOutput);

      if(ruleCache.size() % 100000 == 0 && ruleCache.size() > 0){
        System.out.println("cache size: "+ruleCache.size()+rule.getName());
      }

    }

    @Override
    public void put(Expression<K> input, Expression<K> output) {

      Expression<K> shrunkInput = input.map(new RuleSet.Intern<>(intern), factory);
      Expression<K> shrunkOutput = output.map(new RuleSet.Intern<>(intern), factory);

      if(cache.size() % 100000 == 0 && cache.size() > 0){
        System.out.println("cache size: "+cache.size());
      }

      cache.put(shrunkInput, shrunkOutput);
    }

    @Override
    public ExprFactory<K> factory() {
      return this.factory;
    }

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

    //  TODO pointer
    while (!orig.equals(simplified)) {

//      if(orig.equals(simplified)){
//        System.out.println();
//        System.out.println("y tho");
//        System.out.println(orig);
//        System.out.println(simplified);
//      }

      orig = simplified;
      simplified = applyAllSingle(orig, rules, cache);
    }

    cache.put(e, simplified);

    return simplified;
  }

  private static <K> Expression<K> applyAllSingle(Expression<K> e, List<Rule<?, K>> rules, RuleSetCache<K> cache) {

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

    Expression<K> tmp = e.apply(rules, cache);

//    if(tmp != e && tmp.equals(e)){
//      System.out.println();
//      System.out.println("sigh");
//      System.out.println(tmp);
//      System.out.println(e);
//    }

    for (Rule<?, K> r : rules) {
      Expression<K> input = tmp;

      Expression<K> cached2 = cache.get(r.getClass(), input);
//
      if (cached2 != null) {
        tmp = cached2;
      }else{

        Expression<K> old = tmp;
        tmp = r.apply(tmp, cache);

        if (!old.equals(tmp)){
          cache.put(r.getClass(), input, tmp);
        }

      }

    }

//    cache.put(e, tmp);

    return tmp;
  }

  public static <K> Expression<K> applySet(Expression<K> root, List<Rule<?, K>> allRules) {
    return applyAll(root, allRules, new UnboundedCache<>(new ExprFactory.Interning<>(new HashMap<>())));
  }


}
