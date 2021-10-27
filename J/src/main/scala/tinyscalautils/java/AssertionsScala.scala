package tinyscalautils.java

private final class AssertionsScala:
   def checkNonNull[A <: AnyRef](obj: A): A = tinyscalautils.assertions.checkNonNull(obj)

   def require(condition: Boolean, message: String, args: Array[Object]): Unit =
      tinyscalautils.assertions.require(condition, message, args*)

   def require(condition: Boolean): Unit = tinyscalautils.assertions.require(condition)

   def requireState(condition: Boolean, message: String, args: Array[Object]): Unit =
      tinyscalautils.assertions.requireState(condition, message, args*)

   def requireState(condition: Boolean): Unit = tinyscalautils.assertions.requireState(condition)
