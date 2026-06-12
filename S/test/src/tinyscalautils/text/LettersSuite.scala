package tinyscalautils.text

import org.scalatest.funsuite.AnyFunSuite

class LettersSuite extends AnyFunSuite:

   test("StringLetters") {
      import StringLetters.*

      assert(
        A + B + C + D + E + F + G + H + I + J + K + L + M + N + O + P + Q + R + S + T + U + V + W + X + Y + Z == "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      )
   }

   test("CharLetters") {
      import CharLetters.*

      assert(
        A + B + C + D + E + F + G + H + I + J + K + L + M + N + O + P + Q + R + S + T + U + V + W + X + Y + Z == 13 * ('A' + 'Z')
      )
   }

   test("example") {
      import StringLetters.*

      val list = List(A, B, C)
      assert(list.tail.head == B)
   }
