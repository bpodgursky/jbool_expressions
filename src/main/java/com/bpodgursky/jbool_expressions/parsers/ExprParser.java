package com.bpodgursky.jbool_expressions.parsers;

import com.bpodgursky.jbool_expressions.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

import static com.bpodgursky.jbool_expressions.parsers.BooleanExprParser.*;
import static java.lang.String.format;

public class ExprParser {

  public static Expression<String> parse(String expression) {
    return parse(expression, new IdentityMap());
  }

  public static <T> Expression<T> parse(String expression, TokenMapper<T> mapper) {
    BooleanExprLexer lexer = new BooleanExprLexer(CharStreams.fromString(expression));
    BooleanExprParser parser = new BooleanExprParser(new org.antlr.v4.runtime.CommonTokenStream(lexer));

    return parse((ParserRuleContext) parser.parse().getChild(0), mapper);
  }

  public static <T> Expression<T> parse(ParserRuleContext tree, TokenMapper<T> mapper) {
    if (tree instanceof AndExpressionContext) {
      return And.of(getBinaryExpressions(tree, mapper));
    }
    if (tree instanceof OrExpressionContext) {
      return Or.of(getBinaryExpressions(tree, mapper));
    }
    if (tree instanceof NotExpressionContext) {
      return Not.of(parse((ParserRuleContext) tree.getChild(1), mapper));
    }
    if (tree instanceof IdentifierExpressionContext
            || tree instanceof QuotedIdentifierExpressionContext
            || tree instanceof DoubleQuotedIdentifierExpressionContext) {
      return Variable.of(mapper.getVariable(tree.getText()));
    }
    if (tree instanceof BoolExpressionContext) {
      return Boolean
              .valueOf(tree.start.getText())
              .equals(Literal.getTrue().getValue())
              ? Literal.getTrue()
              : Literal.getFalse();
    }
    if (tree instanceof AtomExpressionContext) {
      return parse((ParserRuleContext) tree.getChild(0), mapper);
    }
    if (tree instanceof ParenExpressionContext) {
      return parse((ParserRuleContext) tree.getChild(1), mapper);
    }
    throw new RuntimeException(
            format("Unrecognized ParserRuleContext: start type: '%s', text: '%s'",
                    tree.start.getType(),
                    tree.getText()));
  }

  private static <T> List<Expression<T>> getBinaryExpressions(ParserRuleContext binaryContext, TokenMapper<T> mapper) {
    List<Expression<T>> binaryChildren = new ArrayList<>();
    ParserRuleContext child0 = (ParserRuleContext) binaryContext.getChild(0);
    if (binaryContext.getClass().equals(child0.getClass())) {
      binaryChildren.addAll(getBinaryExpressions(child0, mapper));
    } else {
      binaryChildren.add(parse(child0, mapper));
    }
    ParserRuleContext child2 = (ParserRuleContext) binaryContext.getChild(2);
    if (binaryContext.getClass().equals(child2.getClass())) {
      binaryChildren.addAll(getBinaryExpressions(child2, mapper));
    } else {
      binaryChildren.add(parse(child2, mapper));
    }
    return binaryChildren;
  }
}
