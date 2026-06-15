# Package Documentation

## Installation

Using `sbt`:

```scala
libraryDependencies += "io.github.charpov" %% "tiny-scala-utils" % "1.9.1"
```

Using `Mill`:

```yaml
mvnDeps:
  - io.github.charpov:tiny-scala-utils::1.9.1
  - ...
```

(Adjust for other Maven-based tools.)

### Note:

Starting with version 1.8, the library is hosted on Maven Central. For earlier versions, you need to use `com.github` instead of `io.github` and add a resolver:

Using `sbt`:

```scala
resolvers += "TinyScalaUtils" at "https://charpov.github.io/TinyScalaUtils/maven"
libraryDependencies += "com.github.charpov" %% "tiny-scala-utils" % "<version>"
```

Using `Mill`:

```yaml
repositories: [https://charpov.github.io/TinyScalaUtils/maven]
```

## Binary compatibility

Java 17 and Scala 3.9, or newer.

## Structure

The library is organized in themed sub-packages:

  - `lang`: general stuff.
  - `assertions`: logical assertions, state/argument preconditions.
  - `control`: additional pseudo control structures.
  - `collection`: collection add-ons (infinite iterators, random selection, ...)
  - `io`: read/write text files, access resources.
  - `text`: text formatting, including thread/time information.
  - `threads`: concurrency stuff.
  - `timing`: timers, delayers, elapsed time calculation.
  - `util`: utility functions, fast random number generators.
