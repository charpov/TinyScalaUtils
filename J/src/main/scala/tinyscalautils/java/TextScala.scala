package tinyscalautils.java

import java.nio.charset.Charset

private final class TextScala:
   def printout(code: Runnable, inclErr: Boolean, inclSys: Boolean, charset: Charset): String =
      tinyscalautils.text.printout(includeErr = inclErr, includeSystem = inclSys, charset = charset)(
        code.run()
      )

   def info(): Unit = tinyscalautils.text.info()

   def plural(x: Number, singularForm: String, pluralForm: String): String =
      tinyscalautils.text.plural(x.doubleValue(), singularForm, pluralForm)

   def plural(x: Number, singularForm: String): String =
      tinyscalautils.text.plural(x.doubleValue(), singularForm)

   def timeString(seconds: Double, unitsCount: Int): String =
      tinyscalautils.text.timeString(seconds, unitsCount)

   def standardMode       = tinyscalautils.text.standardMode
   def timeMode           = tinyscalautils.text.timeMode
   def timeDemoMode       = tinyscalautils.text.timeDemoMode
   def threadMode         = tinyscalautils.text.threadMode
   def threadTimeMode     = tinyscalautils.text.threadTimeMode
   def threadTimeDemoMode = tinyscalautils.text.threadTimeDemoMode
