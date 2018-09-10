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

      long parseTime = 0;
      long simplifyTime = 0;
      long sopTime = 0;
      long posTime = 0;

      int scanned = 0;
      while(scan.hasNext()){
        String expr = scan.nextLine();

        if(++scanned % 1000 == 0){
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

      System.out.println("\tPrefix length:\t"+nameParts[1]);
      System.out.println("\tNum variables:\t"+nameParts[2]);
      System.out.println("\tMin expr length:\t"+nameParts[3]);
      System.out.println("\tMax expr length:\t"+nameParts[4]);
      System.out.println("\tMax depth:\t"+nameParts[5]);
      System.out.println("\t\tTotal parse time:\t"+parseTime);
      System.out.println("\t\tTotal toDNF time:\t"+simplifyTime);
      System.out.println("\t\tTotal sop time:\t"+sopTime);
      System.out.println("\t\tTotal pos time:\t"+posTime);

    }

  }

}

