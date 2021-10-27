package tinyscalautils.io

import java.util.MissingResourceException
import java.net.URL

/** Finds the given resource as a URL.
  *
  * Throws [[java.util.MissingResourceException]] is the resource is not found.
  *
  * @since 1.0
  */
@throws[MissingResourceException]("if the resource is not found")
def findResource(name: String): URL =
   getClass.getClassLoader.getResource(name) match
      case null => throw MissingResourceException("resource not found", name, name)
      case url  => url
