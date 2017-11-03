package com.bpodgursky.jbool_expressions.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;

public class BenchmarkPerformance {

  public static void main(String[] args) throws FileNotFoundException {
    File datasets = new File("datasets");

    for (File file : datasets.listFiles()) {

      Scanner scan = new Scanner(new FileReader(file));

      String[] nameParts = file.getName().split("_");

      System.out.println("Looking at dataset");
      System.out.println("\tPrefix length:\t"+nameParts[1]);
      System.out.println("\tNum variables:\t"+nameParts[2]);
      System.out.println("\tMin expr length:\t"+nameParts[3]);
      System.out.println("\tMax expr length:\t"+nameParts[4]);
      System.out.println("\tMax depth:\t"+nameParts[5]);

      long parseTime = 0;
      long simplifyTime = 0;
      long sopTime = 0;
      long posTime = 0;

      int scanned = 0;
      while(scan.hasNext()){
        String expr = scan.nextLine();

        if(++scanned % 10 == 0){
          System.out.println(scanned+"...");
        }

        long exprStartTime = System.currentTimeMillis();
        Expression<String> parsed = ExprParser.parse(expr);
        long exprParseTime = System.currentTimeMillis();

        RuleSet.simplify(parsed);
        long exprSimplifyTime = System.currentTimeMillis();

        RuleSet.toSop(parsed);
        long exprSopTime = System.currentTimeMillis();

        RuleSet.toPos(parsed);
        long exprPosTime = System.currentTimeMillis();

        parseTime += (exprParseTime - exprStartTime);
        simplifyTime += (exprSimplifyTime - exprParseTime);
        sopTime += (exprSopTime - exprSimplifyTime);
        posTime += (exprPosTime - exprSopTime);

      }

      System.out.println("Total parse time:\t"+parseTime);
      System.out.println("Total simplify time:\t"+simplifyTime);
      System.out.println("Total sop time:\t"+sopTime);
      System.out.println("Total pos time:\t"+posTime);

    }

  }

}
