package com.bpodgursky.jbool_expressions;

public class PrintOptions {
  public enum BooleanOperatorOption {
    AS_SYMBOL, AS_ENGLISH_TEXT_UPPERCASE, AS_ENGLISH_TEXT_LOWERCASE, AS_ENGLISH_TEXT_CAPITALIZE;
  }

  public enum BooleanLiteralOption {
    AS_BINARY, AS_ENGLISH_TEXT_UPPERCASE, AS_ENGLISH_TEXT_LOWERCASE, AS_ENGLISH_TEXT_CAPITALIZE;
  }
  
  public enum VariableNameOption {
    AS_UPPERCASE, AS_LOWERCASE, AS_CAPITALIZE, AS_CREATED;
  }
  
  public enum VariableEscapeOption {
    AS_QUOTED_STRING, AS_DOUBLE_QUOTED_STRING, AS_BACKTICK_STRING, AS_CREATED;
  }

  public enum VariableEscapeApplyOption {
    ALWAYS, ONLY_WHEN_NEEDED, AS_CREATED;
  }
  
  public enum WhitespaceOption {
    AS_SPACE, AS_TAB;
  }

  private BooleanOperatorOption booleanOperatorOption = BooleanOperatorOption.AS_SYMBOL;
  private BooleanLiteralOption booleanLiteralOption = BooleanLiteralOption.AS_ENGLISH_TEXT_LOWERCASE;

  private VariableNameOption variableNameOption = VariableNameOption.AS_CREATED;
  private VariableEscapeOption variableEscapeOption = VariableEscapeOption.AS_CREATED;
  private VariableEscapeApplyOption variableEscapeApplyOption = VariableEscapeApplyOption.AS_CREATED;

  private WhitespaceOption whitespaceOption = WhitespaceOption.AS_SPACE;

  public static PrintOptions withDefaults() {
    return new PrintOptions();
  }

  public BooleanOperatorOption getBooleanOperatorOption() {
    return booleanOperatorOption;
  }

  public void setBooleanOperatorOption(BooleanOperatorOption booleanOperatorOption) {
    this.booleanOperatorOption = booleanOperatorOption;
  }

  public BooleanLiteralOption getBooleanLiteralOption() {
    return booleanLiteralOption;
  }
  
  public void setBooleanLiteralOption(BooleanLiteralOption booleanLiteralOption) {
    this.booleanLiteralOption = booleanLiteralOption;
  }
  
  public VariableNameOption getVariableNameOption() {
    return variableNameOption;
  }
  
  public void setVariableNameOption(VariableNameOption variableNameOption) {
    this.variableNameOption = variableNameOption;
  }
  
  public VariableEscapeOption getVariableEscapeOption() {
    return variableEscapeOption;
  }

  public void setVariableEscapeOption(VariableEscapeOption variableEscapeOption) {
    this.variableEscapeOption = variableEscapeOption;
  }

  public VariableEscapeApplyOption getVariableEscapeApplyOption() {
    return variableEscapeApplyOption;
  }

  public void setVariableEscapeApplyOption(VariableEscapeApplyOption variableEscapeApplyOption) {
    this.variableEscapeApplyOption = variableEscapeApplyOption;
  }

  public WhitespaceOption getWhitespaceOption() {
    return whitespaceOption;
  }

  public void setWhitespaceOption(WhitespaceOption whitespaceOption) {
    this.whitespaceOption = whitespaceOption;
  }

  public PrintOptions withBooleanOperatorOption(BooleanOperatorOption booleanOperatorOption) {
    this.booleanOperatorOption = booleanOperatorOption;
    return this;
  }

  public PrintOptions withBooleanLiteralOption(BooleanLiteralOption booleanLiteralOption) {
    this.booleanLiteralOption = booleanLiteralOption;
    return this;
  }

  public PrintOptions withVariableNameOption(VariableNameOption variableNameOption) {
    this.variableNameOption = variableNameOption;
    return this;
  }
  
  public PrintOptions withVariableEscapeOption(VariableEscapeOption variableEscapeOption) {
    this.variableEscapeOption = variableEscapeOption;
    return this;
  }

  public PrintOptions withVariableEscapeApplyOption(VariableEscapeApplyOption variableEscapeApplyOption) {
    this.variableEscapeApplyOption = variableEscapeApplyOption;
    return this;
  }

  public PrintOptions withWhitespaceOptionOption(WhitespaceOption whitespaceOption) {
    this.whitespaceOption = whitespaceOption;
    return this;
  }
}
