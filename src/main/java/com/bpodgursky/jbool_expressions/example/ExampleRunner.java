package com.bpodgursky.jbool_expressions.example;

import java.util.Collections;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.Variable;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;

public class ExampleRunner {
  public static void main(String[] args) {

    Expression<String> expr = And.of(Variable.of("A"),
        Variable.of("B"),
        Or.of(Variable.of("C"), Not.of(Variable.of("C"))));

    System.out.println(expr);
    //  ((!C | C) & A & B)

    Expression<String> simplified = RuleSet.simplify(expr);

    System.out.println(simplified);
    //  (A & B)

    Expression<String> halfAssigned = RuleSet.assign(simplified, Collections.singletonMap("A", true), ExprOptions.noCaching());
    System.out.println(halfAssigned);
    //  B

    Expression<String> resolved = RuleSet.assign(halfAssigned, Collections.singletonMap("B", true), ExprOptions.noCaching());
    System.out.println(resolved);
    //  true

    System.out.println(expr);
    //  ((!C | C) & A & B)

    Expression<String> parsedExpression = RuleSet.simplify(ExprParser.parse("( ( (! C) | C) & A & B)"));
    System.out.println(parsedExpression);
    System.out.println(parsedExpression.equals(simplified));

    //  (A & B)
    //  true

    Expression<String> nonStandard = ExprParser.parse("( ( A | B) & ( C | D))");
    System.out.println(nonStandard);

    //  ((A | B) & (C | D))

    Expression<String> sopForm = RuleSet.toSop(nonStandard);
    System.out.println(sopForm);

    //  ((A & C) | (A & D) | (B & C) | (B & D))

    Expression<String> nonStandard2 = ExprParser.parse("((A & B) | (C & D))");
    System.out.println(nonStandard2);

    //  ((A & B) | (C & D))

    Expression<String> posForm = RuleSet.toCNF(nonStandard2);
    System.out.println(posForm);

    //  ((A | C) & (A | D) & (B | C) & (B | D))
  }
}
