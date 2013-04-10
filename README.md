Project jbool_expressions
========
jbool_expressions is a simple open-source library for creating and manipulating boolean expressions in java.

Example / Usage
====

A basic propositional expression is built out of the types `And`, `Or`, `Not`, `Variable` and `Literal`.  All of these extend the base type Expression.  For example,

```java
    Expression<String> expr = And.of(
        Variable.of("A"),
        Variable.of("B"),
        Or.of(Variable.of("C"), Not.of(Variable.of("C"))));
    System.out.println(expr);
```
We see the expression is what we expect.  The toString is in prefix notation, where '+' represents `Or` , '*' reprsents `And` and '!' represents `Not`:

```bash
((!C | C) & A & B)
```

### Simplification ###

Of course, this expression contains a useless term (either C or (! C) is always true.)  We can simplify the expression, and see that the extra term is simplified out:

```java
    Expression<String> simplified = RuleSet.simplify(expr);
    System.out.println(expr);
```
outputs:

```bash
(A & B)
```

### Variable Assignment ###

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
((!C | C) & A & B)
```

### Input String Parsing ###

Alternatively, we could have provided our expression as a String in prefix notation and parsed it.  We can verify that this expression is identical to the one we built manually:

```java
    Expression<String> parsedExpression = RuleSet.simplify(ExprParser.parse("( ( (! C) | C) & A & B)"));
    System.out.println(parsedExpression);
    System.out.println(parsedExpression.equals(simplified));
```
output:
```bash
(A & B)
true
```

### Sum-of-products form ###

We can also convert expressions to sum-of-products form instead of just simplifying them.  For example:

```java
    Expression<String> nonStandard = PrefixParser.parse("(* (+ A B) (+ C D))");
    System.out.println(nonStandard);

    Expression<String> sopForm = RuleSet.toSop(nonStandard);
    System.out.println(sopForm);
```

output:

```bash
((A | B) & (C | D))
((A & C) | (A & D) | (B & C) | (B & D))
```

All of these examples can also be found in [ExampleRunner](https://github.com/bpodgursky/jbool_expressions/blob/master/src/main/java/com/bpodgursky/jbool_expressions/example/ExampleRunner.java)

Downloading
====

jbool_expressions is available via maven central:

```xml
<dependency>
    <groupId>com.bpodgursky</groupId>
    <artifactId>jbool_expressions</artifactId>
    <version>1.2</version>
</dependency>
```
Snapshots are available via sonatype:

```xml
<repository>
    <id>sonatype-oss-public</id>
    <url>https://oss.sonatype.org/content/groups/public/</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
</repository>

<dependency>
    <groupId>com.bpodgursky</groupId>
    <artifactId>jbool_expressions</artifactId>
    <version>1.3</version>
</dependency>

```

Building
====

jbool_expressions is built with Maven.  To build from source,

```bash
> mvn package
```

generates a snapshot jar target/jbool_expressions-1.0-SNAPSHOT.jar.

To run the test suite locally,

```bash
> mvn test
```

Development
====

jbool_expressions is very much in-development, and is in no way, shape, or form guaranteed to be stable or bug-free.  Bugs, suggestions, or pull requests are all very welcome.

License
====
Copyright 2013 Ben Podgursky

Licensed under the Apache License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0

