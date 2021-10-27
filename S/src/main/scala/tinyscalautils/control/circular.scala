package tinyscalautils.control

extension [A](iterable: Iterable[A])
   /** An iterator that repeats the elements of the iterable in a circular way.
     *
     * For instance,
     *
     * {{{List(A,B,C).circular}}}
     *
     * is the infinite iterator `A,B,C,A,B,C,A,B,...`.
     *
     * The iterator is empty if the source is empty.
     *
     * @since 1.0
     */
   def circular: Iterator[A] =
      if iterable.isEmpty then Iterator.empty else Iterator.continually(iterable.iterator).flatten
