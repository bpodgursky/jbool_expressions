Project jbool_expressions
========

jbool_expressions is a simple open-source library for creating and manipulating boolean expressions in java.

Example / Usage
====

A basic propositional expression is built out of the types "And", "Or", "Not", "Variable" and "Literal".  All of these extend the base type Expression.  For example,

```java
    Expression<String> expr = And.of(Variable.of("A"),
        Variable.of("B"),
        Or.of(Variable.of("C"), Not.of(Variable.of("C"))));
    System.out.println(expr);
```
We see the expression is what we expect.  The toString is in prefix notation, where '+' represents 'Or' , '*' reprsents 'And' and '!' represents 'Not':

```bash
(* (+ (! C) C) A B)
```

Of course, this expression contains a useless term (either C or (! C) is always true.)  We can simplify the expression, and see that the extra term is simplified out:

```java
    Expression<String> simplified = RuleSet.simplify(expr);
    System.out.println(expr);
```
outputs:

```bash
(* A B)
```

We can assign a value to one of the variables, and see that the expression is simplified after assigning "A" a value:

```java
    Expression<String> halfAssigned = RuleSet.assign(simplified, Collections.singletonMap("A", true));
    System.out.println(halfAssigned);
```
outputs:

```bash
B
```

We can assign the last variable, and see that the expression resolves to a literal "true".

```java
    Expression<String> resolved = RuleSet.assign(halfAssigned, Collections.singletonMap("B", true));
    System.out.println(resolved);

```
outputs:

```bash
true
```

All expressions are immutable (we got a new expression back each time we performed an operation), so we can see that the original expression is unmodified:

```java
    System.out.println(expr);
```
outputs:
```bash
(* (+ (! C) C) A B)
```

Alternatively, we could have provided our expression as a String in prefix notation and parsed it.  We can verify that this expression is identical to the one we built manually:

```java
    Expression<String> parsedExpression = PrefixParser.parse("(* (+ (! C) C) A B)");
    System.out.println(parsedExpression);
    System.out.println(parsedExpression.equals(expr));
```
output:
```bash
(* (+ (! C) C) A B)
true
```

We can also convert expressions to sum-of-products form instead of just simplifying them.  For example:

```java
    Expression<String> nonStandard = PrefixParser.parse("(* (+ A B) (+ C D))");
    System.out.println(nonStandard);

    Expression<String> sopForm = RuleSet.toSop(nonStandard);
    System.out.println(sopForm);
```

output:

```bash
(* (+ A B) (+ C D))
(+ (* A C) (* A D) (* B C) (* B D))
```

Building
====

To build from source,

```bash
>mvn package
```

generates a snapshot jar target/jbool_expressions-1.0-SNAPSHOT.jar.

To run the test suite locally,

```bash
>mvn test
```
