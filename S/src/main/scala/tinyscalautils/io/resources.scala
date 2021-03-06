package tinyscalautils.io

import java.net.URL
import java.util.MissingResourceException

extension [A](clazz: Class[A])
   /** Finds the given resource as a URL. This is simply a call to `getResource` that throws
     * `MissingResourceException` instead of returning `null`.
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
      clazz.getResource(name) match
         case null => throw MissingResourceException("resource not found", name, name)
         case url  => url
