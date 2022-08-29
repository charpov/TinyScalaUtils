# Package Documentation

## Installation

Using `sbt`:

```scala
resolvers += "TinyScalaUtils" at "https://charpov.github.io/TinyScalaUtils/maven/"
libraryDependencies += "com.github.charpov" %% "tiny-scala-utils" % "<version>"
```

(Adjust for other Maven-based tools.)

## Binary compatibility

Java 11 and Scala 3.2, or newer.

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
