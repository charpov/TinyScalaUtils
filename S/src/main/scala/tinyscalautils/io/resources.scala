package tinyscalautils.io

import java.net.URL
import java.util.MissingResourceException
import scala.collection.IterableFactory
import scala.io.Source
import scala.util.Using

extension (obj: AnyRef)
   /** Finds the given resource as a URL. This is simply a call to `getClass.getResource` that
     * throws `MissingResourceException` instead of returning `null`.
     *
     * Like `getResource`, paths that start with a slash are absolute, and relative paths are
     * relative to the full package name.
     *
     * @see
     *   [[java.lang.Class.getResource]]
     *
     * Throws [[java.util.MissingResourceException]] if the resource is not found.
     *
     * @since 1.0
     */
   @throws[MissingResourceException]("if the resource is not found")
   def findResource(name: String): URL =
      obj.getClass.getResource(name) match
         case null => throw MissingResourceException("resource not found", name, name)
         case url  => url

/** Parses a test file (sequence of lines) using a given parser. Lines that parse to an empty
  * sequence (such as `None`) are ignored.
  *
  * @param url
  *   the source to parse.
  *
  * @param parser
  *   the parser to use.
  *
  * @param factory
  *   a factory for the desired collection type (defaults to `List`).
  *
  * @since 1.1
  */
def parseURL[A, C[_]](
    url: URL,
    parser: String => IterableOnce[A],
    factory: IterableFactory[C] = List
): C[A] = Using.resource(Source.fromURL(url))(_.getLines().flatMap(parser).to(factory))
