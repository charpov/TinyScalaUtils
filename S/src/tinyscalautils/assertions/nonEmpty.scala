package tinyscalautils.assertions

extension [A](col: java.util.Collection[A])
   @deprecated("use collections.nonEmpty instead", since = "1.5.0")
   inline def nonEmpty: Boolean = !col.isEmpty
