package tinyscalautils.java

import java.nio.charset.Charset

private final class PrintoutScala:
   def printout(code: Runnable, inclErr: Boolean, inclSys: Boolean, charset: Charset): String =
      tinyscalautils.text.printout(includeErr = inclErr, includeSystem = inclSys, charset = charset)(
        code.run()
      )
