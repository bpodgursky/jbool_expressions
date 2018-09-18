package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.Variable;
import com.bpodgursky.jbool_expressions.eval.EvalEngine;

public class QuineMcCluskey {

  public static class Implicant {

    private final int base;
    private final int dontCare;
    boolean merged;

    public Implicant(int base, int dontCare) {
      this.base = base;
      this.dontCare = dontCare;
    }

    @Override
    public String toString() {
      return "Implicant{" +
          "base=" + base +
          ", dontCare=" + dontCare +
          ", merged=" + merged +
          '}';
    }

    @Override
    public boolean equals(Object o) {

      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Implicant implicant = (Implicant)o;
      return base == implicant.base &&
          dontCare == implicant.dontCare &&
          merged == implicant.merged;
    }

    @Override
    public int hashCode() {
      return Objects.hash(base, dontCare, merged);
    }
  }

  public static <K> Expression<K> toDNF(Expression<K> input) {

    ArrayList<K> variables = new ArrayList<>(ExprUtil.getConstraintsByWeight(input));

    //  expand all true/false inputs
    List<Integer> minterms = findMinterms(0, variables, new HashMap<>(), input);

    if (minterms.size() == Math.pow(2, variables.size())) {
      return Literal.getTrue();
    }

    Set<Implicant> mergedImplicants = getMergedImplicants(minterms);
    EPICalculation essentialPrimeImplicants = getEssentialPrimeImplicants(mergedImplicants, minterms);

    List<Expression<K>> orChildren = new ArrayList<>();

    for (Implicant epi : essentialPrimeImplicants.epis) {
      orChildren.add(toExpr(epi, variables));
    }

    //  use petrick's algorithm
    if (!essentialPrimeImplicants.uncoveredMinterms.isEmpty()) {
      orChildren.addAll(getPetrickMethodImplicants(variables, essentialPrimeImplicants.uncoveredMinterms, new ArrayList<>(essentialPrimeImplicants.nonEpis)));
    }

    if (orChildren.isEmpty()) {
      return Literal.getFalse();
    }

    return RuleSet.simplify(Or.of(orChildren));
  }

  public static class EPICalculation {
    private final Set<Implicant> epis;
    private final Set<Implicant> nonEpis;
    private final List<Integer> uncoveredMinterms;

    private EPICalculation(Set<Implicant> epis, Set<Implicant> nonEPIs, List<Integer> uncoveredMinterms) {
      this.epis = epis;
      this.nonEpis = nonEPIs;
      this.uncoveredMinterms = uncoveredMinterms;
    }

    public Set<Implicant> getEpis() {
      return epis;
    }

    public List<Integer> getUncoveredMinterms() {
      return uncoveredMinterms;
    }
  }

  //  wikipedia example
  public static EPICalculation getEssentialPrimeImplicants(Set<Implicant> implicants, List<Integer> minterms) {

    Set<Implicant> epis = new HashSet<>();
    Set<Implicant> nonEPIs = new HashSet<>(implicants);
    List<Integer> uncoveredMinterms = new ArrayList<>();

    for (Integer minterm : minterms) {

      List<Implicant> coveringImplicants = implicants.stream()
          .filter(implicant -> covers(minterm, implicant))
          .collect(Collectors.toList());

      if (coveringImplicants.size() == 1) {
        Implicant implicant = coveringImplicants.get(0);
        epis.add(implicant);
        nonEPIs.remove(implicant);
      } else {
        uncoveredMinterms.add(minterm);
      }

    }

    List<Integer> remainingMinterms = new LinkedList<>();
    for (Integer uncoveredMinterm : uncoveredMinterms) {

      boolean coveredByEPIs = false;

      for (Implicant epi : epis) {
        if (covers(uncoveredMinterm, epi)) {
          coveredByEPIs = true;
          break;
        }
      }

      if (!coveredByEPIs) {
        remainingMinterms.add(uncoveredMinterm);
      }

    }

    return new EPICalculation(epis, nonEPIs, remainingMinterms);

  }

  private static boolean covers(Integer minterm, Implicant implicant) {
    return (implicant.base ^ (minterm & ~implicant.dontCare)) == 0;
  }


  protected static <K> List<Expression<K>> getPetrickMethodImplicants(List<K> variables, List<Integer> remainingMinterms, List<Implicant> implicants) {

    int i = 0;
    Map<Implicant, Integer> implicantMap = new HashMap<>();
    Map<Integer, Implicant> implicantMapInv = new HashMap<>();
    for (Implicant implicant : implicants) {
      int inc = i++;
      implicantMap.put(implicant, inc);
      implicantMapInv.put(inc, implicant);
    }


    List<Or<Integer>> products = new ArrayList<>();

    //  for each minterm
    for (Integer remainingMinterm : remainingMinterms) {

      //  get all implicants that cover it
      List<Variable<Integer>> coveringImplicants = new ArrayList<>();
      for (Implicant implicant : implicants) {

        if (covers(remainingMinterm, implicant)) {
          coveringImplicants.add(Variable.of(implicantMap.get(implicant)));
        }

      }

      products.add(Or.of(coveringImplicants));

    }

    And<Integer> join = And.of(products);
    Expression<Integer> asSop = RulesHelper.applySet(join, RulesHelper.toSopRules());

    Or<Integer> root = (Or<Integer>)asSop;

    int smallestTerms = Integer.MAX_VALUE;
    Expression<Integer> bestTerm = null;


    for (Expression<Integer> child : root.getChildren()) {

      //  translate this back into the variables we care about

      Set<K> allVars = new HashSet<>();

      for (Integer implicantNum : child.getAllK()) {
        Implicant implicant = implicantMapInv.get(implicantNum);
        Expression<K> expr = toExpr(implicant, variables);
        allVars.addAll(expr.getAllK());
      }

      if (allVars.size() < smallestTerms) {
        bestTerm = child;
        smallestTerms = allVars.size();
      }

    }

    List<Expression<K>> returnOrs = new ArrayList<>();

    if (bestTerm instanceof Variable) {
      Variable<Integer> var = (Variable<Integer>)bestTerm;
      returnOrs.add(toExpr(implicantMapInv.get(var.getValue()), variables));

    } else {

      And<Integer> bestAnd = (And<Integer>)bestTerm;

      for (Expression<Integer> child : bestAnd.getChildren()) {
        Variable<Integer> var = (Variable<Integer>)child;
        returnOrs.add(toExpr(implicantMapInv.get(var.getValue()), variables));
      }
    }

    return returnOrs;

  }

  private static <K> Expression<K> toExpr(Implicant implicant, List<K> variables) {

    List<Expression<K>> children = new ArrayList<>();
    for (int i = 0; i < variables.size(); i++) {

      boolean val = (implicant.base & (1 << i)) != 0;
      boolean dontcare = (implicant.dontCare & (1 << i)) != 0;

      if (!dontcare) {

        if (val) {
          children.add(Variable.of(variables.get(i)));
        } else {
          children.add(Not.of(Variable.of(variables.get(i))));
        }

      }

    }

    return And.of(children);

  }

  private static Set<Implicant> getImplicants(Integer numOnes, Integer sizeN, Map<Integer, Map<Integer, Set<Implicant>>> implicants) {

    if (implicants.containsKey(numOnes)) {
      Map<Integer, Set<Implicant>> oneMores = implicants.get(numOnes);

      if (oneMores.containsKey(sizeN)) {
        return oneMores.get(sizeN);
      }

    }

    return Collections.emptySet();
  }

  protected static Set<Implicant> getMergedImplicants(List<Integer> minterms) {

    Map<Integer, Map<Integer, Set<Implicant>>> implicants = groupMinterms(minterms);

    int sizeN = 1;
    while (true) {

      boolean mergedAnything = false;
      for (Integer numOnes : implicants.keySet()) {

        //  for each number of ones, get implicants of sizeN

        Set<Implicant> newImplicants = new HashSet<>();

        for (Implicant implicant : getImplicants(numOnes, sizeN, implicants)) {
          for (Implicant potentialJoin : getImplicants(numOnes + 1, sizeN, implicants)) {

            //  determine if implicants differ by only one 1
            if (implicant.dontCare == potentialJoin.dontCare) {
              int difference = implicant.base ^ potentialJoin.base;
              if (numberOfSetBits(difference) == 1) {

                implicant.merged = true;
                potentialJoin.merged = true;

                newImplicants.add(new Implicant(implicant.base & ~difference, implicant.dontCare | difference));

              }

            }

          }
        }

        if (!newImplicants.isEmpty()) {
          mergedAnything = true;
          implicants.get(numOnes).put(sizeN * 2, newImplicants);
        }

      }

      if (!mergedAnything) {
        break;
      }

      sizeN *= 2;

    }

    Set<Implicant> unmerged = new HashSet<>();

    for (Map.Entry<Integer, Map<Integer, Set<Implicant>>> entry : implicants.entrySet()) {

      for (Map.Entry<Integer, Set<Implicant>> entry2 : entry.getValue().entrySet()) {

        for (Implicant implicant : entry2.getValue()) {
          if (!implicant.merged) {
            unmerged.add(implicant);
          }
        }

      }
    }

    return unmerged;
  }

  private static Map<Integer, Map<Integer, Set<Implicant>>> groupMinterms(List<Integer> minterms) {
    Map<Integer, List<Integer>> mintermsByOnes = new HashMap<>();

    for (Integer minterm : minterms) {
      int bits = numberOfSetBits(minterm);
      if (!mintermsByOnes.containsKey(bits)) {
        mintermsByOnes.put(bits, new ArrayList<>());
      }
      mintermsByOnes.get(bits).add(minterm);
    }

    Map<Integer, Map<Integer, Set<Implicant>>> numOnesToNumImplicantsToImplicants = new HashMap<>();

    for (Map.Entry<Integer, List<Integer>> entry : mintermsByOnes.entrySet()) {

      Set<Implicant> implicants = new HashSet<>();
      for (Integer minTerm : entry.getValue()) {
        implicants.add(new Implicant(minTerm, 0));
      }

      numOnesToNumImplicantsToImplicants.put(entry.getKey(), new HashMap<>(Collections.singletonMap(1, implicants)));
    }

    return numOnesToNumImplicantsToImplicants;
  }


  //  https://stackoverflow.com/a/109025/599558
  private static int numberOfSetBits(int i) {
    i = i - ((i >>> 1) & 0x55555555);
    i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
    return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
  }

  public static <K> List<Integer> findMinterms(int pos, ArrayList<K> variables, Map<K, Boolean> assignments, Expression<K> input) {
    List<Integer> minterms = new ArrayList<>();
    findMinterms(pos, variables, input, assignments, minterms);
    return minterms;
  }

  static AtomicLong evaled = new AtomicLong();

  public static <K> void findMinterms(int pos, ArrayList<K> variables, Expression<K> input, Map<K, Boolean> assignments, List<Integer> collectedMinterms) {

    if (pos == variables.size()) {

      Literal val = (Literal)input;

      evaled.incrementAndGet();

      if(evaled.get() % 1000000 == 0){
        System.out.println("Evaluated minterms: "+evaled.get());
      }

      if (val.getValue()) {
        //  evaluate
        int minTerm = 0;

        for (int i = 0; i < variables.size(); i++) {

          if (assignments.get(variables.get(i))) {
            minTerm |= 1 << i;
          }

        }

        collectedMinterms.add(minTerm);

        if (collectedMinterms.size() % 100000 == 0) {
          System.out.println();
          System.out.println(System.currentTimeMillis());
          System.out.println(minTerm + "\t" + collectedMinterms.size());
        }

        return;

      } else {
        return;
      }
    }

    assignments.put(variables.get(pos), true);
    findMinterms(pos + 1, variables, RuleSet.assign(input, Collections.singletonMap(variables.get(pos), true)), assignments, collectedMinterms);

    assignments.put(variables.get(pos), false);
    findMinterms(pos + 1, variables, RuleSet.assign(input, Collections.singletonMap(variables.get(pos), false)), assignments, collectedMinterms);

  }

}
