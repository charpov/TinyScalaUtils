package tinyscalautils.java

import java.nio.charset.Charset

private final class TextScala:
   def printout(code: Runnable, inclErr: Boolean, inclSys: Boolean, charset: Charset): String =
      tinyscalautils.text.printout(includeErr = inclErr, includeSystem = inclSys, charset = charset)(
        code.run()
      )

   def info(): Unit = tinyscalautils.text.info()
