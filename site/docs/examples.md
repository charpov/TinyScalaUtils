---
title: Code Examples
---
# Code Examples 

## Package `lang`

### `unit`

A `unit` value is defined instead of the usual `()` token for improved readability, e.g.:

```scala
f(((), ())) // hard to parse

import tinyscalautils.lang.unit

f((unit, unit)) // easier
```

### `InterruptibleConstructor` / `InterruptibleEquality`

Checks the interrupted status of the current thread before instance creation and/or equality and hash code:

```scala
import tinyscalautils.lang.{ InterruptibleConstructor, InterruptibleEquality }

class C extends InterruptibleConstructor, InterruptibleEquality

val c = C() // throws InterruptedException if thread is interrupted
c == c // throws InterruptedException if thread is interrupted
c.## // throws InterruptedException if thread is interrupted
```

## Package `control`

### `times`

This is the same `times` as in `Scalactic`:

```scala
import tinyscalautils.control.times

3 times {
   println("Beetlejuice!")
}   
```

### `circular`

Produces an infinite collection of elements by continuously repeating a finite collection:

```scala
import tinyscalautils.control.circular

val i: Iterator[Int] = List(1, 2, 3).circular
// i is an infinite iterator: 1,2,3,1,2,3,1,2,3,...

i.take(10).toList // the list 1,2,3,1,2,3,1,2,3,1
```

### `limitedStack` / `StackOverflowException`

`StackOverflowException` can replace `StackOverflowError` where the latter would not adequately be caught:

```scala
import tinyscalautils.lang.StackOverflowException
import tinyscalautils.control.limitedStack

def f(x: Int): Int = 1 + f(x + 1)

val t = Try(f(0)) // dies with StackOverflowError, no usable t value

val t = Try(limitedStack(f(0)))
t.isFailure // true
t.failed.get.isInstanceOf[StackOverflowException] // true
```

### `interruptibly`

Checks the interrupted status of the current thread before running some code:

```scala
import tinyscalautils.control.interruptibly

while true do interruptibly {
   x += 1
}   
```
The infinite loops terminates with `InterruptedException` if the thread is interrupted.

## Package `assertions`

### `require` / `requireState`

Simple precondition checking:

```scala
import tinyscalautils.assertions.require

// Scala style:
require(cond, s"bad argument because ${someMethod()}")

// Java style:
require(cond, "bad argument because %s", someMethod())
```
In either case, `someMethod()` is only evaluated if the condition is false.

When a requirement fails, `require` throws `IllegalArgumentException` while `requireState` throws `IllegalStateException`.

### `checkNonNull`

A utility to help reject `null` with `IllegalArgumentException` instead of `NullPointerException`:

```scala
require(str.nonEmpty, "string cannot be empty") // throws NPE if str is null, Java style

import tinyscalautils.assertions.checkNonNull

require(checkNonNull(str).nonEmpty, "string cannot be empty") // throws IllegalArgumentException on null or empty string
```

### `implies`

Logical implication:

```scala
val s: IndexedSeq[Int] = ...
require(!s.indices.contains(i) || s(i) > 0)

import tinyscalautils.assertions.implies

require(s.indices.contains(i) implies s(i) > 0)
```
The evaluation is short-circuited: RHS is evaluated only if LHS is true.
Operator also available under the symbolic name `==>`.

### `in`

An infix operator that swaps the arguments of `contains`:

```scala
import tinyscalautils.assertions.in

value in (1 to 10) // same as (1 to 10) contains value
```

## Package `text`

### `StringLetters/CharLetters`

All cap letters as strings or characters:

```scala
import StringLetters.*

val list = List(A, B, C)
assert(list.tail.head == B)
```

### `short/pad`

Shortens or pads strings:

```scala
import tinyscalautils.text.{ short, pad }

val str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
str.short // "ABCDEFGHIJKLMNOPQRSTUVWXYZabc..."
str.short(10) // "ABCDEFG..."
"X".pad(5) // "    X"
"X".pad(5, padding = '.') // "....X"
```

Note that standard method `padTo` pads strings _on the right_.

### `printf/println`

Adds thread and/or time information to print statements:

```scala
   import tinyscalautils.text.{ println, timeMode }
   println("foo") // prints: at 13:02:13.207: foo
   
   import tinyscalautils.text.{ println, timeDemoMode }
   println("foo") // prints: at XX:XX:13.207: foo
   
   import tinyscalautils.text.{ println, threadMode }
   println("foo") // prints: main: foo
   
   import tinyscalautils.text.{ println, threadTimeMode }
   println("foo") // prints: main at 13:02:13.207: foo
   
   import tinyscalautils.text.{ println, threadTimeDemoMode }
   println("foo") // prints: main at XX:XX:13.207: foo

   import tinyscalautils.text.{ println, standardMode }
   println("foo") // prints: foo
   
   import tinyscalautils.text.{ println, silentMode }
   println("foo") // prints nothing
```

Function `printf` works similarly.

### `printout`

Captures the output of print statements into a string:

```scala
val str = printout {
   Console.out.print("hello")
   Console.err.println(" world")
} // the string "hello"

val str = printout(includeErr = true) {
   Console.out.print("hello")
   Console.err.println(" world")
} // the string "hello world\n"   
```

By default, only `Console.out` (and `Predef.print` and `tinyscalautils.text.print`, which are based on it) is captured, but `System.out` and `System.err` are *not* captured.
This can be changed with an option:

```scala
val str = printout(includeSystem = true) {
   Console.out.print("hello")
   System.out.println(" world")
} // the string "hello world\n"   
```

Note that `System.out` and `System.err` are global variables, shared among threads, while `Console.out` can be different for different threads.
To capture the output of newly created threads, these threads should be created within the `printout` function.

## Package `timing`

### `sleep`

Suspends the running thread for a specified number of seconds:

```scala
import tinyscalautils.timing.{ getTime, sleep }

sleep(3.0) // sleeps 3 seconds, starting from now

val start = getTime()
... 
sleep(3.0, start) // sleeps 3 seconds, starting from start time
```

This last example won't sleep at all if more than 3 seconds have already elapsed since time `start`.

This method `sleep` does not undershoot even if `Thread.sleep` does, and does not throw `InterruptedException`.

### `delay`

Delays returning a value:

```scala
import tinyscalautils.timing.delay

val result = delay(3.0) {
   // code
}  
```

This produces the same value as `code`, but takes 3 seconds (assuming the evaluation of `code` took less than 3 seconds).
Like `sleep`, also exists in a `start` variant: `delay(3.0, start)(code)`.

### `timeOf`

The time it took to evaluate some code, in seconds:

```scala
import tinyscalautils.timing.timeOf

val time = timeOf {
   // code
}   
```

The value produced by the code, if any, is ignored.

### `timeIt`

The value produced by some code, and the time it took to compute it, as a pair:

```scala
import tinyscalautils.timing.timeIt

val (value, time) = timeIt {
   // code
}   
```

### Timers

Simple timers.
Can be used explicitly:

```scala
import tinyscalautils.threads.Executors

val timer = Executors.newTimer(size = 2)
val future = timer.schedule(1.5) {
   // code 
}
```

or implicitly:

```scala
import tinyscalautils.threads.{ DelayedFuture, Executors }

given Timer = Executors.newTimer(2)
val future = DelayedFuture(1.5) {
   // code
}
```

Note that timers need to be explicitly shut down for their threads to terminate.

### `zipWithDuration`

Asynchronously adds duration to a future:

```scala
import tinyscalautils.timing.zipWithDuration

val future: Future[String] = ...
val f: Future[(String, Double)] = future.zipWithDuration
```

The second half of the pair is the duration (in seconds) between the invocation of `zipWithFuture` and the completion of `future`.

### `slow`

A mechanism to slow down sources of values:

```scala
import tinyscalautils.timing.SlowIterator

val i = Iterator.range(0, 1000)

val sum = i.sum // 499500
val sum = i.slow(10.0).sum // 499500, but in about 10 seconds

// first 10 elements delayed by about 1 second, then fast:
i.slow(10.0, delayedElements = 10)

// first 100 elements delayed by about 0.1 second, then fast
i.slow(10.0, delayedElements = 100)

// all elements delayed by about 0.001 second, then 9-second delay to finish
i.slow(10.0, delayedElements = 10_000)
```

Also available on `Source` and `LazyList`.

## Package `threads`

### `orTimeout`

```scala
import tinyscalautils.threads.{ orTimeout, completeOnTimeout } 

val f: Future[Int] = ...

// completes with value of f, or TimeoutException after 3 seconds
val f1: Future[Int] = f.orTimeout(3.0)

// same, but executes cancelCode after the timeout
val f2: Future[Int] = f.orTimeout(3.0, cancelCode = ...)

// completes with value of f, or of altCode if f times out after 3 seconds
// if f completes during the execution of altCode, the value of f is used
val f3: Future[Int] = f.completeOnTimeout(3.0)(altCode)

// completes with value of f, or of altCode if f times out after 3 seconds
// if f completes during the execution of altCode, the value of altCode is used
val f4: Future[Int] = f.completeOnTimeout(3.0, strict = true)(altCode)
```

### `zipWithDuration`

```scala
import tinyscalautils.timing.zipWithDuration

val f: Future[String] = ...
val tf: Future[(String, Double)] = f.zipWithDuration
```

The second half of the pair is the duration, in seconds, between the call to `zipWithDuration` and the completion of `f`.

### `Executors`

A facility to more easily create thread pools with customized thread factories and/or rejected execution handlers.
All pools have type `ExecutionContextExecutorService` for easier use of both Java and Scala features.

```scala
import tinyscalautils.threads.Executors

// same as java.util.concurrent.Executors.newFixedThreadPool
val exec = Executors.newThreadPool(4)

// sets a rejection policy
val exec = Executors.withRejectionPolicy(rejectionPolicy).newThreadPool(4)

// sets a "discard" rejection policy
val exec = Executors.silent.newThreadPool(4)

// sets a thread facyory
val exec = Executors.withFactory(threadFactory).newThreadPool(2)

// sets a rejection policy and a thread factory
val exec = Executors
   .withRejectionPolicy(rejectionPolicy)
   .withFactory(threadFactory)
   .newThreadPool(4)

// sets a thread factory and a rejection policy (equivalent as above)
val exec = Executors
   .withFactory(threadFactory)
   .withRejectionPolicy(rejectionPolicy)
   .newThreadPool(4)

// same as java.util.concurrent.Executors.newCachedThreadPool
val exec = Executors.newUnlimitedThreadPool(60.0)

// same as above, but terminates idle threads after 1 second instead of 1 minute
val exec = Executors.newUnlimitedThreadPool()

// sets a rejection policy and a thread factory, as with newThreadPool
val exec = Executors
   .withRejectionPolicy(rejectionPolicy)
   .withFactory(threadFactory)
   .newUnlimitedThreadPool()
```

A `global` implicit is defined as an unlimited pool with a 1-second keep-alive time (and non-daemon threads):

```scala
given ExecutionContext = tinyscalautils.threads.Executors.global

val f = Future { ... } // runs on global thread pool
```

### `run`/`Run`

Executes code on an executor:

```scala
import tinyscalautils.threads.{ run, Run }

given exec: Executor = ...

exec.run {
   // code
}

Run {
   // code
}      
```

Both functions return `Unit` (no future).

### `KeepThreadsFactory`

A thread factory that keeps a reference on all the threads it creates:

```scala
import tinyscalautils.threads.{ KeepThreadsFactory, Executors }

val tf = KeepThreadsFactory()
val exec = Executors.withFactory(threadFactory).newUnlimitedThreadPool()

// use the exec pool...

val threads = tf.allThreads // all created threads so far, in order of creation
```

The collection returned by `allThreads` is live: threads newly created by the factory will be added to it.
The factory can be reset: threads created before the reset are discarded.

### `MarkedThreadsFactory`

A cheaper alternative to `KeepThreadsFactory`: All the threads produced by the factory are distinguished by having type `MarkedThread`:

```scala
import tinyscalautils.threads.{ MarkedThreadsFactory, Executors }

given exec: ExecutionContextExecutorService =
   Executors.withFactory(MarkedThreadsFactory).newThreadPool(4)

val future = Future(Thread.currentThread())
val runner = Await.result(future, Duration.Inf)
runner.isInstanceOf[MarkedThread] // true
```

For convenience, an extension can be used to check for `MarkedThread` type:

```scala
import tinyscalautils.threads.isMarkedThread

runner.isMarkedThread // true
```

### `shutdownAndWait`

Combines `shutdown`, `shutdownNow` and `awaitTermination` in one method:

```scala
import tinyscalautils.threads.shutdownAndWait

val exec: ExecutorService = ...

if exec.shutdownAndWait(5.0)
   then // shutdown was invoked, and pool terminated within 5 seconds
   else // shutdown was invoked, but pool did not terminate within 5 seconds

if exec.shutdownAndWait(5.0, force = true)
   then // shutdown was invoked, and pool terminated within 5 seconds
   else // shutdown was invoked, then after 5 seocnds, shutdownNow was invoked
```

### `countDownAndWait`

Combines `countDown` and `await`:

```scala
import tinyscalautils.threads.countDownAndWait

val latch: CountDownLatch = ...

latch.countDownAndWait()
```

Waiting uses a one-second timeout by default.

### `joined`

Combines `join` and `isAlive`:

```scala
import tinyscalautils.threads.joined

if thread.joined(3.0)
   then ... // thread is terminated (or never started)
   else ... // 3-second timeout; thread may still be running                
```

### `newThread`

Kotlin-like functions for easier thread creation:

```scala
import tinyscalautils.threads.newThread 

val thread = newThread(name = "Joe", start = false, daemon = true, waitForChildren = false) {
   // code
}   
```

All arguments have default values and are optional.

## Package `io`

### `listPaths`

The contents of a directory, as a list:

```scala
import tinyscalautils.io.listPaths

for (path: Path <- listPaths(dir)) do ...
```

A list is used instead of a stream so the directory can be closed.

### `listLines`

The contents of a UTF8 text file, as a list:

```scala
import tinyscalautils.io.listLines

for (line: String <- listLines(file)) do ...
```

A list is used instead of a stream so the file can be closed.

## Package `util`

### `FastRandom`

Faster random number generators that don't rely on the `java.util.Random` thread-safe implementation.
Individual instances are not thread-safe, but the singleton `FastRandom` can be shared among threads without contention:

```scala
import tinyscalautils.util.FastRandom
impot scala.util.Random

val rand: Random = FastRandom(42L) // fast, but not thread-safe

val rand: Random = FastRandom // fast, thread-safe, but cannot be seeded
```

The implementation relies on `ThreadLocalRandom` and `SplittableRandom`, available in Java 11, not on the fancier generators that were added to Java 17. 

### `log2`

Base 2 integer logarithm:

```scala
import tinyscalautils.util.log2

log2(1) // 0
log2(7) // 2
log2(8) // 3
```

### `pickOne/pickOneOption`

Picks an element at random:

```scala
import tinyscalautils.util.{pickOne, pickOneOption}

val col: Iterable[A] = ...

val one: A = col.pickOne
val oneOpt: Option[A] = col.pickOneOption
```

Uses a given `Random` in scope.

### `average`

Calculates an average by ignoring a fixed number of low/high values:

```scala
val nums: Seq[BigDecimal] = Seq(1, 7, 9, 11, 12, 14)

average(nums) // (1 + 7 + 9 + 11 + 12 + 14) / 6
average(nums, 1) // (7 + 9 + 11 + 12) / 4
average(nums, 2) // (9 + 11) / 2
average(nums, 3) // 0
```

The function works on any `Fractional` type, including `Double`.
