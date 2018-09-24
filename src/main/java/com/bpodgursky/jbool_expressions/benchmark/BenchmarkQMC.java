package com.bpodgursky.jbool_expressions.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.eval.EvalEngine;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.QuineMcCluskey;
import com.bpodgursky.jbool_expressions.rules.RuleSet;

public class BenchmarkQMC {

  public static void main(String[] args) throws FileNotFoundException {

    File datasets = new File("datasets");

    for (File file : datasets.listFiles()) {

      Scanner scan = new Scanner(new FileReader(file));

      String[] nameParts = file.getName().split("_");

      System.out.println("Looking at dataset");

      long sopTime = 0;
      long qmcTime = 0;


      System.out.println("\tPrefix length:\t"+nameParts[1]);
      System.out.println("\tNum variables:\t"+nameParts[2]);
      System.out.println("\tMin expr length:\t"+nameParts[3]);
      System.out.println("\tMax expr length:\t"+nameParts[4]);
      System.out.println("\tMax depth:\t"+nameParts[5]);

      int scanned = 0;
      while(scan.hasNext()){
        String expr = scan.nextLine();

        if(++scanned % 100 == 0){
          System.out.println(scanned+"...");
          System.out.println("\t\tsop time:\t"+sopTime);
          System.out.println("\t\tQMC time:\t"+qmcTime);
        }

        Expression<String> parsed1 = ExprParser.parse(expr);
        Expression<String> parsed2 = ExprParser.parse(expr);

        long beforeQMC = System.currentTimeMillis();
        Expression<String> qmcDnf = QuineMcCluskey.toDNF(parsed2);
        long afterQMC = System.currentTimeMillis();

        long beforeSop = System.currentTimeMillis();
        Expression<String> dnf = RuleSet.toSop(parsed1);
        long afterSop = System.currentTimeMillis();

        qmcTime += (afterQMC - beforeQMC);
        sopTime += (afterSop - beforeSop);

        if(!checkEquivalent(dnf, qmcDnf)){
          throw new RuntimeException();
        }

      }


      System.out.println("\t\tTotal sop time:\t"+sopTime);
      System.out.println("\t\tTotal QMC time:\t"+qmcTime);
    }




  }


  public static <K> boolean checkEquivalent(Expression<K> a, Expression<K> b){

    Set<K> allVariables = new HashSet<>();
    allVariables.addAll(a.getAllK());
    allVariables.addAll(b.getAllK());

    ArrayList<K> variables = new ArrayList<>(allVariables);

    return expand(variables, 0, a, b, new HashMap<K, Boolean>());

  }

  private static <K> boolean expand(ArrayList<K> variables, int i, Expression<K> a, Expression<K> b, Map<K, Boolean> assignments) {

    if (i == variables.size()){
      return EvalEngine.evaluateBoolean(a, assignments) == EvalEngine.evaluateBoolean(b, assignments);
    }

    assignments.put(variables.get(i), true);
    if(!expand(variables, i+1, a, b, assignments)){
      return false;
    }

    assignments.put(variables.get(i), false);
    if(!expand(variables, i+1, a, b, assignments)){
      return false;
    }

    return true;

  }


}
