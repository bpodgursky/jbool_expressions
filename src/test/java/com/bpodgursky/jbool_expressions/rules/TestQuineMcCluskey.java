package com.bpodgursky.jbool_expressions.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.JBoolTestCase;
import com.bpodgursky.jbool_expressions.Or;

public class TestQuineMcCluskey extends JBoolTestCase {

  public void testFindMinterms() {

    Expression<String> expr = expr("(A & B) | C");

    ArrayList<String> vars = new ArrayList<>(expr.getAllK());
    Collections.sort(vars);

    List<Integer> minterms = QuineMcCluskey.findMinterms(0, vars, new HashMap<>(), expr);

    Collections.sort(minterms);
    assertEquals(Arrays.asList(3, 4, 5, 6, 7), minterms);
  }

  public void testGetEPIs() {

    List<Integer> minterms = Arrays.asList(
        4, 8, 9, 10, 12, 11, 14, 15
    );

    Set<QuineMcCluskey.Implicant> implicants = QuineMcCluskey.getMergedImplicants(minterms);

    System.out.println(implicants);

    QuineMcCluskey.EPICalculation epis = QuineMcCluskey.getEssentialPrimeImplicants(implicants, Arrays.asList(
        4, 8, 10, 11, 12, 15
    ));

    Set<QuineMcCluskey.Implicant> epiTerms = epis.getEpis();
    System.out.println(epiTerms);
    assertEquals(2, epiTerms.size());
    assertTrue(epiTerms.contains(new QuineMcCluskey.Implicant(4, 8)));
    assertTrue(epiTerms.contains(new QuineMcCluskey.Implicant(10, 5)));

  }

  //  example from https://en.wikipedia.org/wiki/Petrick%27s_method
  public void testPetrick() {

    List<Expression<String>> expr = QuineMcCluskey.getPetrickMethodImplicants(
        Arrays.asList("C", "B", "A"),
        Arrays.asList(0, 1, 2, 5, 6, 7),
        Arrays.asList(
            new QuineMcCluskey.Implicant(0, 1),
            new QuineMcCluskey.Implicant(0, 2),
            new QuineMcCluskey.Implicant(1, 4),
            new QuineMcCluskey.Implicant(2, 4),
            new QuineMcCluskey.Implicant(5, 2),
            new QuineMcCluskey.Implicant(6, 1)

        )
    );

    assertTrue(
        Arrays.asList(
            expr("(!A & !B)"),
            expr("(B & !C)"),
            expr("(A & C)")
        ).equals(expr) ||

            Arrays.asList(
                expr("(!A & !C)"),
                expr("(!B & C)"),
                expr("A & B")
            ).equals(expr)

    );

  }

  //  example from https://en.wikipedia.org/wiki/Petrick%27s_method
  public void testAll() {

    Expression<String> expr = expr("(!A & !B) | (!A & !C) | (!B & C) | (B & !C) | (A & C) | (A & B)");

    Expression<String> simplified = QuineMcCluskey.simplify(expr);

    assertTrue(
        Or.of(Arrays.asList(
            expr("(!A & !B)"),
            expr("(B & !C)"),
            expr("(A & C)")
        )).equals(simplified) ||

            Or.of(Arrays.asList(
                expr("(!A & !C)"),
                expr("(!B & C)"),
                expr("(A & B)")
            )).equals(simplified)

    );

  }

  public void testGetMergedImplicants() {

    Set<QuineMcCluskey.Implicant> implicants = QuineMcCluskey.getMergedImplicants(Arrays.asList(
        5, 7, 9, 11, 13, 15
    ));

    assertEquals(2, implicants.size());

    assertTrue(implicants.contains(new QuineMcCluskey.Implicant(9, 6)));
    assertTrue(implicants.contains(new QuineMcCluskey.Implicant(5, 10)));


    Set<QuineMcCluskey.Implicant> implicants2 = QuineMcCluskey.getMergedImplicants(Arrays.asList(
        2, 3, 6, 7, 8, 10, 11, 12, 14, 15
    ));

    assertEquals(2, implicants2.size());

    assertTrue(implicants2.contains(new QuineMcCluskey.Implicant(2, 13)));
    assertTrue(implicants2.contains(new QuineMcCluskey.Implicant(8, 6)));

  }

}