package com.bpodgursky.jbool_expressions.benchmark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.Variable;
import com.google.common.collect.Lists;

public class GenerateExpressionExamples {


  public static void main(String[] args) throws IOException {

    //  write to file

    generateExpressionsFile(
        "datasets",
        1,
        5,
        2,
        10, 
        6
    );

  }

  private static void generateExpressionsFile(String outputDir,
                                              int variablePrefixLength,
                                              int numVariables,
                                              int minExprLength,
                                              int maxExprLength,
                                              int maxDepth) throws IOException {
    Random rand = new Random();

    new File(outputDir).mkdirs();

    ArrayList<String> variables = Lists.newArrayList();

    String prefix = String.join("", Collections.nCopies(variablePrefixLength, "X"));
    for (int i = 0; i < numVariables; i++) {
      variables.add(prefix + i);
    }

    File output = new File("datasets/expressions_"
        + variablePrefixLength + "_"
        + numVariables + "_"
        + minExprLength + "_"
        + maxExprLength + "_"
        + maxDepth + ".txt"
    );

    FileWriter writer = new FileWriter(output);

    for (int i = 0; i < 50000; i++) {
      Expression<String> expr = recurse(rand, variables, minExprLength, maxExprLength, 0, maxDepth);
      writer.write(expr.toString() + "\n");
    }

    writer.close();

  }

  private static Expression<String> recurse(Random rand,
                                            ArrayList<String> variables,
                                            int minExprLength,
                                            int maxExprLength,
                                            int depth,
                                            int maxDepth) {

    int next;

    //  no point in returning a variable
    if (depth == 0) {
      next = rand.nextInt(3);
    }
    //  force a terminal variable
    else if (depth == maxDepth) {
      next = 3;
    }
    //  choose randomly
    else {
      next = rand.nextInt(4);
    }

    //  and / or
    if (next == 0 || next == 1) {

      List<Expression<String>> terms = Lists.newArrayList();

      for (int i = 0; i < minExprLength + rand.nextInt(maxExprLength - minExprLength); i++) {
        terms.add(recurse(rand, variables, minExprLength, maxExprLength, depth + 1, maxDepth));
      }

      if (next == 0) {
        return And.of(terms);
      } else {
        return Or.of(terms);
      }

    }

    //  not
    else if (next == 2) {
      return Not.of(recurse(rand, variables, minExprLength, maxExprLength, depth + 1, maxDepth));
    }

    //  variable
    else if (next == 3) {
      return Variable.of(variables.get(rand.nextInt(variables.size())));
    }

    //  can't get here
    throw new IllegalArgumentException();

  }

}

