package tinyscalautils.collection

extension [A](col: java.util.Collection[A])
   /** Adds a `nonEmpty` method on Java collections.
     *
     * @since 1.2
     */
   inline def nonEmpty: Boolean = !col.isEmpty
