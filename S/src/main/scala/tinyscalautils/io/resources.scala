package tinyscalautils.io

import java.io.{ IOException, InputStream }
import java.net.{ URI, URL }
import java.util.MissingResourceException
import java.util.zip.GZIPInputStream
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
   def findResource(name: String): URL =
      searchResource(obj, name, trygz = false).getOrElse:
         throw MissingResourceException("resource not found", name, name)

   /** Finds the given resource as a URL. This is simply a call to `getClass.getResource` that falls
     * back to a default location instead of returning `null`. Note that this `findResource` variant
     * does _not_ throw `MissingResourceException`.
     *
     * Like `getResource`, paths that start with a slash are absolute, and relative paths are
     * relative to the full package name. Paths are always treated as relative to the URI when the
     * fallback is used.
     *
     * @see
     *   [[java.lang.Class.getResource]]
     *
     * @param fallback
     *   a fallback URI, which must be absolute.
     *
     * @since 1.3
     */
   def findResource(fallback: URI)(name: String): URL =
      searchResource(obj, name, trygz = false).getOrElse(url(fallback, name))

   /** Finds the given resource as a stream. If the resource is not found, then `name.gz` is tried
     * instead and, if found, opened as GZIP compressed data. (If `name` already ends with `.gz`, no
     * attempt is made with a `.gz.gz` name.)
     *
     * Like `getResource`, paths that start with a slash are absolute, and relative paths are
     * relative to the full package name.
     *
     * @see
     *   [[java.lang.Class.getResource]]
     *
     * Throws [[java.util.MissingResourceException]] if the resource is not found.
     * @since 1.3
     */
   def findResourceAsStream(name: String): InputStream =
      openURL:
         searchResource(obj, name, trygz = true).getOrElse:
            throw MissingResourceException("resource not found", name, name)

   /** Finds the given resource as a stream. If the resource is not found, then `name.gz` is tried
     * instead. If neither is found locally, the fallback location is used to search for `name.gz`
     * first and, if unsuccessful, for `name`. When a `.gz` file is found, either locally or
     * remotely, it is opened as GZIP compressed data. If `name` already ends with `.gz`, no attempt
     * is made with a `.gz.gz` name.
     *
     * Like `getClass.getResource`, paths that start with a slash are absolute, and relative paths
     * are relative to the full package name.
     *
     * @see
     *   [[java.lang.Class.getResource]]
     * @since 1.3
     */
   def findResourceAsStream(fallback: URI)(name: String): InputStream =
      searchResource(obj, name, trygz = true) match
         case Some(url) => openURL(url)
         case None =>
            if name.endsWith(".gz") then openURL(url(fallback, name))
            else
               try openURL(url(fallback, name + ".gz"))
               catch case _: IOException => openURL(url(fallback, name))

private def searchResource(obj: AnyRef, name: String, trygz: Boolean) =
   Option(obj.getClass.getResource(name)) match
      case None if trygz && !name.endsWith(".gz") => Option(obj.getClass.getResource(name + ".gz"))
      case other                                  => other

private def url(uri: URI, name: String) = uri.resolve(name.dropWhile(_ == '/')).toURL

private def openURL(url: URL): InputStream =
   if url.getPath.endsWith(".gz") then GZIPInputStream(url.openStream()) else url.openStream()

@deprecated("use readAll instead", since = "1.3")
def parseURL[A, C[_]](
    url: URL,
    parser: String => IterableOnce[A],
    factory: IterableFactory[C] = List
): C[A] = Using.resource(Source.fromURL(url))(_.getLines().flatMap(parser).to(factory))
