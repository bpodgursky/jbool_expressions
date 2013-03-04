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
We see the expression is what we expect.  The toString is in prefix notation, where '+' represents 'Or' , '*' reprsents 'And' and '!' represents 'Not'.

```bash
>(* (+ (! C) C) A B)
```

Of course, this expression contains a useless term (either C or (! C) is always true.)  We can simplify the expression:

```java
    Expression<String> simplified = RuleSet.simplify(expr);
    System.out.println(expr);
```

We can see that the extra term is simplified out:

```bash
>(* A B)
```

We can assign a value to one of the variables::

```java
    Expression<String> halfAssigned = RuleSet.assign(simplified, Collections.singletonMap("A", true));

    System.out.println(halfAssigned);

```

We see the expression is simplified after assigning "A" a value:

```bash
>B
```

We can assign the last variable:

```java
    Expression<String> resolved = RuleSet.assign(halfAssigned, Collections.singletonMap("B", true));

    System.out.println(resolved);

```

And we see that the expression has resolved to a literal "true":

```bash
>true
```

All expressions are immutable (we got a new expression back each time we performed an operation), so we can see that the original expression is unmodified:

```java
    System.out.println(expr);
```

```bash
>(* (+ (! C) C) A B)
```


Building
====

To build from source,

```bash
>mvn package
```

To run the test suite locally,

```bash
>mvn test
```
