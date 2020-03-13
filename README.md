# HOW TO BUILD

By default, Maven will look for the current JDK version and will build using the configuration of that version.

You can see the current maven profile using:

```bash
mvn clean <goal> help:active-profiles
```
 
If you want to indicate the compile version:

## Using JDK <= 1.8
```bash
mvn clean <goal> -Pjdk8
```

## Using JDK >= 1.9
```bash
mvn clean <goal> -Pjdk8gt
```

# COMPILER API AND AST MANIPULATION
* https://www.javadoc.io/doc/org.kohsuke.sorcerer/sorcerer-javac/latest/index.html
* https://www.baeldung.com/java-build-compiler-plugin
* https://www.javacodegeeks.com/2015/09/java-compiler-api.html
* https://openjdk.java.net/groups/compiler/doc/compilation-overview/index.html
* https://openjdk.java.net/groups/compiler/guide/compilerAPI.html
* https://openjdk.java.net/groups/compiler/doc/hhgtjavac/index.html
* https://openjdk.java.net/groups/compiler/
* http://scg.unibe.ch/archive/projects/Erni08b.pdf

# DOCUMENTATION FOR CLASS LOADERS
* https://www.baeldung.com/java-classloaders
* http://tutorials.jenkov.com/java-reflection/dynamic-class-loading-reloading.html