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

3 times:
   println("Beetlejuice!")
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

while true do interruptibly:
   x += 1
```

The infinite loops terminates with `InterruptedException` if the thread is interrupted.

## Package `collection`

### `circular`

Produces an infinite collection of elements by continuously repeating a finite collection:

```scala
import tinyscalautils.collection.circular

val i: Iterator[Int] = List(1, 2, 3).circular
// i is an infinite iterator: 1,2,3,1,2,3,1,2,3,...

i.take(10).toList // the list 1,2,3,1,2,3,1,2,3,1
```

### `shuffle`

A convenient way yo invoke `Random.shuffle` in a pipeline:

```scala
import tinyscalautils.collection.shuffle

given Random = ...

val s = Seq
   .fill(...)(...)
   .map(...)
   .shuffle
   .zipWithIndex
```

instead of:

```scala
val s = rand.shuffle( 
   Seq.fill(...)(...)
      .map(...)
   ).zipWithIndex   
```

### `randomly`

Produces an infinite collection of elements randomly selected from a finite collection:

```scala
import tinyscalautils.collection.randomly

given Random = ...

val i: Iterator[Int] = List(1, 2, 3).randomly
// i is an infinite iterator of 1s, 2s and 3s, in a random order
```

### `pickOne/pickOneOption`

Produces a randomly selected element from a finite, non-empty collection:

```scala
import tinyscalautils.collection.pickOne

given Random = ...

val n: Int = List(1, 2, 3).pickOne
// n is one of 1, 2 or 3, randomly chosen
```

Exists also as `pickOneOption` to handle empty collections.
These methods should not be used in a loop over the same collection; use `randomly` instead.

### `JavaList`

Like `List.of` but with mutable lists:

```scala
import tinyscalautils.collection.JavaList

val list = JavaList.of("X", "Y") // a mutable list
```

Lists are `ArrayList` by default, but `LinkedList` can be used as well:

```scala
import tinyscalautils.collection.JavaList
import tinyscalautils.collection.LinkedList.factory

val list = JavaList.of("X", "Y") // a mutable linked list
```

### `pollOption` / `peekOption`

Like `poll` / `peek` but wrapping `null` in an option.

```scala
import tinyscalautils.collection.{ pollOption, peekOption }

val q = util.ArrayDeque[String]()
q.offer("X")
q.peekOption   // Some("X")
q.pollOption() // Some("X")
q.pollOption() // None
```

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

### `StringLetters` / `CharLetters`

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

### `print` / `printf` / `println`

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

Functions `print` and `printf` work similarly.
These functions can also be imported as:

```scala
import tinyscalautils.text.threadTimeMode.println
```

which is simpler for most usages (i.e., when not using mode arguments explicitly).

### `printout`

Captures the output of print statements into a string:

```scala
// the string "hello"
val str = printout:
   Console.out.print("hello")
   Console.err.println(" world")

// the string "hello world\n"
val str = printout(includeErr = true):
   Console.out.print("hello")
   Console.err.println(" world")
```

By default, only `Console.out` (and `Predef.print` and `tinyscalautils.text.print`, which are based on it) is captured, but `System.out` and `System.err` are *not* captured.
This can be changed with an option:

```scala
// the string "hello world\n"
val str = printout(includeSystem = true):
   Console.out.print("hello")
   System.out.println(" world")
```

Note that `System.out` and `System.err` are global variables, shared among threads, while `Console.out` can be different for different threads.
To capture the output of newly created threads, these threads should be created within the `printout` function.

### `plural`

Plural forms:

```scala
import tinyscalautils.text.plural

plural(1, "cat")           // "cat"
plural(2, "DOG")           // "DOGS"
plural(2.3, "platypus")    // "platypuses"
plural(4, "mouse", "mice") // "mice"
```

### timeString

Human-friendly representation of a duration expressed in seconds:

```scala
import tinyscalautils.text.timeString

timeString(180.02) // "3 minutes, 20 milliseconds"
timeString(3609.732) // "1 hour, 10 seconds"
timeString(3609.732, unitsCount = 3) // "1 hour, 9 seconds, 732 milliseconds"
timeString(3609.732, unitsCount = 1) // "1 hour"
```

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

This method `sleep` does not undershoot even if `Thread.sleep` does, and does not throw `InterruptedException` (but leaves interrupted threads interrupted).

Non-positive values incur no delay.

### `delay`

Delays returning a value:

```scala
import tinyscalautils.timing.delay

val result = delay(3.0):
   // code
```

This produces the same value as `code`, but takes 3 seconds (assuming the evaluation of `code` took less than 3 seconds).
Like `sleep`, also exists in a `start` variant: `delay(3.0, start)(code)`.

Non-positive values incur no delay.

### `timeOf`

The time it took to evaluate some code, in seconds:

```scala
import tinyscalautils.timing.timeOf

val time = timeOf:
   // code
```

The value produced by the code, if any, is ignored.

### `timeIt`

The value produced by some code, and the time it took to compute it, as a pair:

```scala
import tinyscalautils.timing.timeIt

val (value, time) = timeIt:
   // code
```

### Timers

Simple timers.
Can be used explicitly:

```scala
import tinyscalautils.threads.Executors

val timer = Executors.newTimer(size = 2)

timer.execute(2.5):
   // code
   
val future = timer.schedule(1.5):
   // code
```

or implicitly:

```scala
import tinyscalautils.threads.{ DelayedFuture, Executors, ExecuteAfter }

given Timer = Executors.newTimer(2)

ExecuteAfter(2.5):
   // code

val future = DelayedFuture(1.5):
   // code
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

def i = Iterator.range(0, 1000)

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

### `runFor` / `callFor`

Runs iterative code with a time bound:

```scala
import tinyscalautils.threads.Timer
import tinyscalautils.timing.{ runFor, callFor }

given Timer = ...

val start: T = ...
def step(st: T): Option[T] = ...

// runs step until None, at most 1 minute; returns true if no timeout
runFor(60.0)(start)(step) 

// same but also returns a Seq[T] of step results
callFor(60.0)(start)(step) 
```

Also a simpler form with no carried state:

```scala
def step(): Boolean = ...
runFor(60.0)(step)
```
and richer forms that separate state from produced values:

```scala
val start: T = ...
def step(st: T): Option[(A, T)] = ...

runFor(60.0)(start)(step) // a Boolean and a Seq[A]
```

```scala
val start: T = ...
def step(st: T): (A, Option[T]) = ...

runFor(60.0)(start)(step) // a Boolean and a Seq[A]
```

This last variant produces a last value when it terminates (i.e., `(last, None)`).

## Package `threads`

### `orTimeout`

Adds a timeout feature to future, similar to Java's:

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

### `run` / `Execute/ExecuteAfter`

Executes code on an executor:

```scala
import tinyscalautils.threads.{ run, Execute, ExecuteAfter }

given exec: Executor = ...

exec.run:
   // code

Execute:
   // code
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

### `awaitTermination`

A variant of the standard `awaitTermination` method in which the timeout is optional and specified in seconds:

```scala
import tinyscalautils.threads.awaitTermination

val exec: ExecutorService = ...

if exec.awaitTermination(5.0) ... // waits at most 5 seconds
if exec.awaitTermination() ...    // waits indefinitely
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
   else // shutdown was invoked, then after 5 seconds, shutdownNow was invoked
   
exec.shutdownAndWait() // invokes shutdown and waits indefinitely; force flag is ignored   
```

### `await`

Adds a variant of `await` on `CountDownLatch` that specifies its timeout in seconds:

```scala
import tinyscalautils.threads.await

val latch: CountDownLatch = ...

latch.await(5.0)
```

### `countDownAndWait`

Combines `countDown` and `await`:

```scala
import tinyscalautils.threads.countDownAndWait

val latch: CountDownLatch = ...

latch.countDownAndWait()
latch.countDownAndWait(3.0)
```

Waits forever (interruptibly) if no timeout is specified.

### `acquire`

Adds a variant of `acquire` on `Semaphore` that specifies its timeout in seconds:

```scala
import tinyscalautils.threads.acquire

val sem: Semaphore = ...

sem.acquire(1, seconds = 3.0)
```

### `offer` / `pollOption`

Adds variant of `offer` and `poll` on blocking queues that specifies their timeout in seconds and wrap `null` in an option:

```scala
import tinyscalautils.threads.{ offer, pollOption }

val q = ArrayBlockingQueue[String](1)
q.offer("X", seconds = 1.0) // true, immediately
q.offer("X", seconds = 1.0) // false, after 1 second
q.pollOption(seconds = 1.0) // Some("X"), immediately
q.pollOption(seconds = 1.0) // None, after 1 second
```

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

val thread = newThread(name = "Joe", start = false, daemon = true, waitForChildren = false):
   // code
```

All arguments have default values and are optional.
Be aware that `waitForChildren` only works if children are created within the same thread group (which is not always the case for the default thread factory of `java.util.concurrent.Executors`).

### Execution contexts

Easy setup of execution contexts, mostly for testing:

```scala
import tinyscalautils.threads.withThreadsAndWait

val result = withThreadsAndWait(4):
   Future:
      42
```

This creates a 4-thread pool, runs the future on it, waits for the future to finish, shuts down the thread pool, and sets `result` to 42.
Additionally, if `awaitTermination` is set to true, the constructs also waits for the thread pool to terminate before setting the result.
All waiting is done without a timeout.
Exists also as a `withUnlimitedThreadsAndWait` variant for an unbounded thread pool.

The preceding constructs require the code to produce a future.
For a "fire-and-forget" approach, use `withThreads` and `withUnlimitedThreads`, which can use an arbitrary code.

These constructs create and shut down a new thread pool.
To reuse an existing thread pool, use `withThreadPoolAndWait`:

```scala
import tinyscalautils.threads.withThreadPoolAndWait

val exec = Executors.newUnlimitedThreadPool()
val result = withThreadPoolAndWait(exec):
   Future:
      42
```

This runs the future on the thread pool and waits for the future to finish before setting `result` to 42.
The thread pool is left as-is.
Alternatively, setting `shutdown` to true shuts down the thread pool after the future has completed (but does not not wait for the thread pool to terminate).

Note that if `shutdown` is known to be false at compile time, the thread pool doesn't need a `shutdown` method.
In particular, it can be of type `Executor` or `ExecutionContext`:

```scala
withThreadPoolAndWait(ExecutionContext.global, shutdown = false):
   Future:
      ...
```

This runs the future on the global execution context, and waits for its termination.

### `runAsync`

Run code synchronously in another thread:

```scala
import tinyscalautils.threads.runAsync

val result = runAsync(code)
```

The thread is specified as an implicit `Executor` or `ExecutionContext`.

The purpose of this function is to run non-interruptible code interruptibly.

## Package `io`

### `listPaths`

The contents of a directory, as a list:

```scala
import tinyscalautils.io.listPaths

for path: Path <- listPaths(dir) do ...
```

If a list type is not suitable, use `readPaths` instead:

```scala
import tinyscalautils.io.readPaths

val paths: Set[Path] = readPaths(Set)(dir)
```

*DO NOT* use a stream for the collection.
Since the directory is closed before this function finishes, lazily evaluated collections won't work.

### `listLines`

The contents of a UTF8 text file, as a list:

```scala
import tinyscalautils.io.listLines

for line: String <- listLines(file) do ...
```

If a list type is not suitable, use `readLines` instead:

```scala
import tinyscalautils.io.readLines

val lines: IndexedSeq[String] = readLines(IndexedSeq)(file)
```

*DO NOT* use a stream for the collection.
Since the file is closed before this function finishes, lazily evaluated collections won't work.

### `findResource`

```scala
import tinyscalautils.io.findResource

val url = this.findResource(name)
```

This is equivalent to `getClass.getResource(name)`, except that it throws an exception for missing resources (instead of returning `null`).
In particular, a leading slash in the resource name makes it an absolute path, instead of being associated with the package name by default.

### `parseURL`

```scala
def parse(line: String): Option[Int] = ...
val list: List[Int] = parseURL(url, parse)
```

If a list type is not suitable, specify a factory:

```scala
val seq: IndexedSeq[Int] = parseURL(url, parse, IndexedSeq)
```


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

### `nextInt`

A Kotlin-inspired alternative to `pickOne` to select random numbers from a range:

```scala
import tinyscalautils.util.nextInt
impot scala.util.Random

val rand: Random = ...

rand.nextInt(1 to 10)
rand.nextInt(0 until 100)
```

This looks nicer than `(1 to 10).pickone(using rand)` and `(0 until 100).pickone(using rand)`. 

### `log2`

Base 2 integer logarithm:

```scala
import tinyscalautils.util.log2

log2(1) // 0
log2(7) // 2
log2(8) // 3
log2(1L << 62) // 62
```

### `average`

Calculates an average by ignoring a fixed number of low/high values:

```scala
val nums: Seq[BigDecimal] = Seq(12, 1, 7, 11, 14, 9)

average(nums)    // (1 + 7 + 9 + 11 + 12 + 14) / 6
average(nums, 1) // (7 + 9 + 11 + 12) / 4
average(nums, 2) // (9 + 11) / 2
average(nums, 3) // 0
```

The function works on any `Fractional` type, including `Double`.
