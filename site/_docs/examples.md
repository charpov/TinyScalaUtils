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

Functions or arity 1 and 2 can also be made interruptible via extensions.
A similar loop can be written as:

```scala
import tinyscalautils.control.InterruptiblyExtensions

def f(n: Int) = x += n
val g = f.interruptibly

while true do g(1)
```

Finally, higher-order functions can have their function argument be made interruptible:

```scala
col.map.interruptibly(f)
```

behaves like:

```scala
col.map(f.interruptibly)
```

This is only implemented for functions of arity 1 and 2, which covers the most common cases:

```scala
col.forall.interruptibly(f)
col.foreach.interruptibly(f)
col.fold(start).interruptibly(f)
...
```

Note that:

```scala
for x <- col do interruptibly(f(x))
```

does not check for interrupts if `col` is empty, while:

```scala
col.foreach.interruptibly(f)
```

does.

### `before`

An SML-like operator that evaluates two arguments and returns the value of the first one:

```scala
import tinyscalautils.control.before

var c = 0
c before (c += 1) // returns 0, c is now 1
```


## Package `collection`

### `in`

An infix operator that swaps the arguments of `contains`:

```scala
import tinyscalautils.collection.in

value in (1 to 10) // same as (1 to 10) contains value
```

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

### `JavaSet`

Like `Set.of` but with mutable sets:

```scala
import tinyscalautils.collection.JavaSet

val set = JavaSet.of("X", "Y") // a mutable set
```

Sets are `HashSet` by default, but `TreeSet` can be used as well:

```scala
import tinyscalautils.collection.JavaSet
import tinyscalautils.collection.TreeSet.factory

val set = JavaSet.of("X", "Y") // a mutable tree set
```

### `sortedInReverse`

Like `sorted`, but in reverse:

```scala
import tinyscalautils.collection.sortedInReverse

val seq = ...       // a sequence
seq.sortedInReverse // same sequence as seq.sorted.reverse, but more efficient
```

### `last/lastOption`

Adds `last/lastOption` to `IterableOnce`:

```scala
import tinyscalautils.collection.{ last, lastOption }

Iterator(1, 2, 3).last // 3
Iterator.empty.lastOption // None
```

### `updatedWith`

Replaces a value in a sequence.
The new value is computed from the old value, as an option.
If the option if empty, the old value is removed:

```scala
import tinyscalautils.collection.{ deleted, updatedWith }

val list = List(A, B, C)
list.updatedWith(1)(c => Some(c.toInt))               // List(A, 66, C)
list.updatedWith(1)(c => Option.when(c > Z)(c.toInt)) // List(A, C)
```

Removal can also be achieved using `deleted`:

```scala
list.deleted(1) // List(A, C)
```

### `nonEmpty`

Brings `nonEmpty` to Java collections:

```scala
import tinyscalautils.collection.nonEmpty

val col = java.util.HashSet[Int]()
col.nonEmpty // false
```

### `allDistinct`

Checks that a collection contains no duplicates:

```scala
import tinyscalautils.collection.allDistinct
Seq(1, 2, 3, 4).allDistinct // true
Seq(1, 2, 3, 2).allDistinct // false
Seq.empty.allDistinct       // true
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

timer.run(2.5):
   // code
   
val future = timer.schedule(1.5):
   // code
```

or implicitly:

```scala
import tinyscalautils.threads.{ DelayedFuture, Executors, RunAfter }

given Timer = Executors.newTimer(2)

RunAfter(2.5):
   // code

val future = DelayedFuture(1.5):
   // code
```

Note that timers need to be explicitly shut down for their threads to terminate.
Timers implement the `AutoCloseable` interface.

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

// first 10 elements delayed by about 1 second each, then fast:
i.slow(10.0, delayedElements = 10)

// first 100 elements delayed by about 0.1 second each, then fast
i.slow(10.0, delayedElements = 100)

// all elements delayed by about 0.001 second each, then 9-second delay to finish
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

// same, but runs cancelCode after the timeout
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

### `run` / `Run/RunAfter`

Runss code on an executor:

```scala
import tinyscalautils.threads.{ run, Run, RunAfter }

given exec: Executor = ...

exec.run:
   // code

Run:
   // code
   
RunAfter(2.5):
   // code   
```

All functions return `Unit` (no future).

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
   
exec.shutdownAndWait() // invokes shutdown and waits indefinitely; returns true
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

### `offer` / `poll`

Adds variant of `offer` and `poll` on blocking queues that specifies their timeout in seconds:

```scala
import tinyscalautils.threads.{ offer, poll }

val q = ArrayBlockingQueue[String](1)
q.offer("X", seconds = 1.0) // true, immediately
q.offer("X", seconds = 1.0) // false, after 1 second
q.poll(seconds = 1.0)       // "X", immediately
q.poll(seconds = 1.0)       // null, after 1 second
```

### `joined`

Combines `join` and `isAlive`:

```scala
import tinyscalautils.threads.joined

if thread.joined(3.0)
   then ... // thread is terminated (or never started)
   else ... // 3-second timeout; thread may still be running                
```

### `isSpinning`

Checks if a thread is spinning:

```scala
import tinyscalautils.threads.isSpinning

if thread.isSpinning(seconds = 5.0, threshold = 0.1) then ...
```

The method detects if a thread has spent more than a specified threshold of time (here, 0.1 or 10%) using CPU during a specified time span (here, 5 seconds). The default is to check for 1 second using a 1% threshold.

This functionality may of may not be supported by a given platform (see `java.lang.management.ThreadMXBean`).

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
import tinyscalautils.threads.withThreads

val result = withThreads(4):
   Future(42)
```

This creates a 4-thread pool, runs the future on it, waits for the future to finish, shuts down the thread pool, and sets `result` to 42.
Additionally, if `awaitTermination` is set to true, the constructs also waits for the thread pool to terminate before setting the result.
All waiting is done without a timeout.
Exists also as a `withThreads()` variant for an unbounded thread pool.

The construct can also be used with code that does not produce a future.
It returns the value produces by the code.

Instead of creating (and shutting down) a new thread pool, the construct can also reuse an existing thread pool:

```scala
val exec = Executors.newUnlimitedThreadPool()
val result = withThreads(exec):
   Future(42)
```

This runs the future on the thread pool and waits for the future to finish before setting `result` to 42.
The thread pool is left as-is.
Alternatively, setting `shutdown` to true shuts down the thread pool after the future has completed (but does not not wait for the thread pool to terminate).

Note that if `shutdown` is known to be false at compile time, the thread pool doesn't need a `shutdown` method.
In particular, it can be of type `Executor` or `ExecutionContext`:

```scala
withThreads(ExecutionContext.global, shutdown = false):
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

### `read/readAll`

The contents of a UTF8 text file, as a collection of line elements:

```scala
import tinyscalautils.io.{ readAll, FileIsInput }

def parse(line: String): Option[Int] = ...

readAll(IndexedSeq)(file, parse)     // an IndexedSeq[Int]
readAll(IndexedSeq)(file)            // all non-blank lines
readAll(IndexedSeq)(file, noParsing) // all lines
```

If no factory is specified, it defaults to `List`, e.g.:

```scala
readAll(file, parse)     // a List[Int]
readAll(file)            // a List[String]
readAll(file, noParsing) // a List[String]
```

`read` does no parsing and returns the entire file contents as a single string.

The argument `file` must belong to the `Input` type class, which is predefined to contain `InputStream`, `URL`, `URI`, `Path`, `File` and `String` (as a filename or a URL).

*DO NOT* use a stream for the collection.
Since the source is closed before this function finishes, lazily evaluated collections won't work.

### `readingAll`

This is a variant of `readAll` that returns values as a closeable iterator:

```scala
import tinyscalautils.io.{ readingAll, FileIsInput }

def parse(line: String): Option[Int] = ...

Using.resource(readingAll(file, parse)): i =>
   // i has type Iterator[Int]
// file input closed here
```
`readingAll` does not support a silent mode.

### `write/writeAll`

Write data to a UTF8 text file:

```scala
   import tinyscalautils.io.{ write, writeAll, FileIsOutput }
   
   val list = List(1, 2, 3)

   write(file)(list)                                      // file contains "List(1, 2, 3)"
   write(file, newline = true)(list)                      // file contains "List(1, 2, 3)\n"
   writeAll(file)(list)                                   // file contains "1\n2\n3\n"
   writeAll(sep = ",")(file)(list)                        // file contains "1,2,3"
   writeAll(pre = "[", sep = ",", post = "]")(file)(list) // file contains "[1,2,3]"
   writeAll()(file)(list)                                 // file contains "123"
```
Note that the simplified `writeAll` variant includes a final newline after the last value.

The argument `file` must belong to the `Output` type class, which is predefined to contain `OutputStream`, `Path`, `File` and `String` (as a filename).


### `findResource/findResourceAsStream`

```scala
import tinyscalautils.io.findResource

val url = this.findResource(name)
```

This is equivalent to `getClass.getResource(name)`, except that it throws an exception for missing resources (instead of returning `null`).
In particular, a leading slash in the resource name makes it an absolute path, instead of being associated with the package name by default.

```scala
val url = this.findResource(fallback)(name)
```

This variant uses a fallback URI instead of failing with `MissingResourceException`.

The `findResourceAsStream` variants work in the same way but looks for a GZIP compressed variant of a resource before giving up. 

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

The implementation relies on `ThreadLocalRandom` and `SplittableRandom`. 

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

### `interruptible`

Makes all the methods of `Random` interruptible, i.e.,
`rand.interruptible.nextInt()` is the same as `interruptibly(rand.nextInt())`.

Note that `rand.interruptible.self` does *not* check for interrupts.

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

### `dot/star`

An identity function that prints a single dot (or star):

```scala
import tinyscalautils.util.dot

val x = dot(y)  // x eq y and a dot was printed
val x = star(y) // x eq y and a star was printed
```

Importing `tinyscalautils.text.silentMode` stops the dot/star from being printed.
Printing modes other than `silent` and `standard` cannot be used.

### `isZero`

A zero test on numeric types:

```scala
import tinyscalautils.util.isZero

val n: Long = 0
n.isZero && !(n + 1).isZero // true

val n: BigDecimal = 0
n.isZero && !(n + 1).isZero // true
```

The intent is to be more efficient (maybe) than `n == 0` on some types.

### `isEven/isOdd`

Check if numbers are even/odd:

```scala
import tinyscalautils.util.{ isEven, isOdd }

val n = 2
n.isEven      // true
(n + 1).isOdd // true
```

This is implemented for both `Int` and `Long`.

### `pow`

Integer power of a number:

```scala
val n: Int = ...
val m: BigDecimal = ...
val p: Double = ...

n.pow(7)
m.pow(7)
p.pow(7)
```

This is implemented for the `Numeric` classtype, with optimizations for common types.
