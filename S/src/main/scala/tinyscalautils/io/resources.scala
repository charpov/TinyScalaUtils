package tinyscalautils.io

import java.net.URL
import java.util.MissingResourceException

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
