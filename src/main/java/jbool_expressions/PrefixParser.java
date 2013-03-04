package jbool_expressions;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//  TODO don't be such a luddie, and actually implement this using CUP or something
//  TODO this doesn't necessarily fail on invalid input
public class PrefixParser {

  private static final Pattern WHITESPACE = Pattern.compile("(\\s+).*", Pattern.DOTALL);
  private static final Pattern VARIABLE = Pattern.compile("([A-Z]+).*", Pattern.DOTALL);
  private static final Pattern TRUE = Pattern.compile("(true).*", Pattern.DOTALL);
  private static final Pattern FALSE = Pattern.compile("(false).*", Pattern.DOTALL);
  private static final Pattern NOT = Pattern.compile("(!).*", Pattern.DOTALL);
  private static final Pattern OR = Pattern.compile("(\\+).*", Pattern.DOTALL);
  private static final Pattern AND = Pattern.compile("(\\*).*", Pattern.DOTALL);

  private static final List<Pattern> TOKENS = Arrays.asList(
    VARIABLE, TRUE, FALSE, NOT, AND, OR
  );

  public static LinkedList<String> tokenize(String expr){
    LinkedList<String> parts = Lists.newLinkedList();

    int i = 0;

    while(i < expr.length()){
      int iBefore = i;
      String remaining = expr.substring(i);

      Matcher whitespace = WHITESPACE.matcher(remaining);
      if(whitespace.matches()){
        i+=whitespace.group(1).length();
      }

      for(Pattern p: TOKENS){
        Matcher var = p.matcher(remaining);
        if(var.matches()){
          String variable = var.group(1);
          parts.add(variable);
          i+=variable.length();
        }
      }

      if(remaining.charAt(0) == '('){
        int nest = 1;
        int chars = 1;
        while(nest > 0){
          if(remaining.charAt(chars) == '('){
            nest++;
          }
          else if(remaining.charAt(chars) == ')'){
            nest--;
          }
          chars++;
        }

        parts.add(remaining.substring(0, chars));
        i+=chars;
      }

      if(i == iBefore){
        throw new RuntimeException();
      }
    }
    return parts;
  }

  public static Expression<String> parse(String expression){

    Matcher var = VARIABLE.matcher(expression);
    var.matches();

    if(var.matches()){
      return Variable.of(var.group(1));
    }

    Matcher truem = TRUE.matcher(expression);
    if(truem.matches()){
      return Literal.getTrue();
    }

    Matcher falsem = FALSE.matcher(expression);
    if(falsem.matches()){
      return Literal.getFalse();
    }

    String insideParen = expression.substring(1, expression.length()-1);
    LinkedList<String> parts = tokenize(insideParen);

    String part1 = parts.pop();
    List<Expression<String>> args = Lists.newArrayList();
    for(String part: parts){
      args.add(parse(part));
    }

    if(AND.matcher(part1).matches()){
      return And.of(args);
    }
    else if(OR.matcher(part1).matches()){
      return Or.of(args);
    }
    else if(NOT.matcher(part1).matches()){
      return Not.of(args.get(0));
    }

    throw new RuntimeException("no operand found for: "+expression);
  }
}
