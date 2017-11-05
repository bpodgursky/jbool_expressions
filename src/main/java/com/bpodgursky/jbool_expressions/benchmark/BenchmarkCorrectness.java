package com.bpodgursky.jbool_expressions.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;

public class BenchmarkCorrectness {
  public static void main(String[] args) throws IOException {
    File datasets = new File("datasets");

    for (File file : datasets.listFiles()) {

      Scanner scan = new Scanner(new FileReader(file));
      FileWriter results = new FileWriter("/tmp/JBoolBenchmarkCorrectness_"+System.currentTimeMillis()+".txt");

      System.out.println("Looking at dataset");

      int scanned = 0;
      while(scan.hasNext()){
        String expr = scan.nextLine();

        if(++scanned % 1000 == 0){
          System.out.println(scanned+"...");
        }

        Expression<String> parsed = ExprParser.parse(expr);
        Expression<String> posStr = RuleSet.toPos(parsed);
        results.write(posStr+"\n");

      }

      results.close();

    }

  }
}
