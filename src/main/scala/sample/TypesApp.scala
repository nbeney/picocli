package sample

import java.io.File
import java.math.BigInteger
import java.net.{InetAddress, URL}
import java.time._
import java.util.Currency

import picocli.CommandLine.{Command => Cmd, Option => Opt, Parameters => Par}
import sample.utils.{PicocliCommandLine, PicocliSimpleCommand}

object TypesApp extends App {
  new PicocliCommandLine(new Command).
    registerConverter(classOf[Fraction], Fraction.unapply(_)).
    registerConverter(classOf[WeekDays.Value], WeekDays.withName(_)).
    run(args)

  @Cmd(name = "types-app", description = "A simple app showing various option types.")
  class Command extends PicocliSimpleCommand {
    @Opt(names = "-v, --verbose", description = "run in verbose mode")
    var verbose = false

    @Opt(names = "-s, --string", paramLabel = "TEXT", description = "a string (default: ${DEFAULT-VALUE})")
    var string = "picocli - a mighty tiny command line interface"

    @Opt(names = "-i, --int", paramLabel = "INT", description = "an integer (default: ${DEFAULT-VALUE})")
    var int = 123

    @Opt(names = "-d, --double", paramLabel = "DBL", description = "a double (default: ${DEFAULT-VALUE})")
    var double = 3.1415

    @Opt(names = "--big-dec", paramLabel = "BIG-DEC", description = "a BigDecimal (default: ${DEFAULT-VALUE})")
    var bigDec = BigDecimal("123456789.987654321")

    @Opt(names = "--big-int", paramLabel = "BIG-INT", description = "a BigInteger (default: ${DEFAULT-VALUE})")
    var bigInt = BigInteger.TEN

    @Opt(names = "-f, --file", paramLabel = "PATH", description = "a file (default: ${DEFAULT-VALUE})")
    var file = new File("config.xml")

    @Opt(names = "-D, --date", paramLabel = "DATE", description = "a date (default: ${DEFAULT-VALUE})")
    var date = LocalDate.now

    @Opt(names = "-T, --time", paramLabel = "TIME", description = "a time (default: ${DEFAULT-VALUE})")
    var time = LocalTime.now.withNano(0)

    @Opt(names = "--datetime", paramLabel = "DATETIME", description = "a datetime (default: ${DEFAULT-VALUE})")
    var datetime = LocalDateTime.now.withNano(0)

    @Opt(names = "--duration", paramLabel = "DURATION", description = "a duration (default: ${DEFAULT-VALUE})")
    var duration = Duration.parse("P1DT12H30M")

    @Opt(names = "--period", paramLabel = "PERIOD", description = "a period (default: ${DEFAULT-VALUE})")
    var period = Period.parse("P1M15D")

    @Opt(names = "--currency", paramLabel = "CNCY", description = "a currency (default: ${DEFAULT-VALUE})")
    var currency = Currency.getInstance("JPY")

    @Opt(names = "--inet-addr", paramLabel = "ADDR", description = "an InetAddress (default: ${DEFAULT-VALUE})")
    var inetAddr = InetAddress.getLocalHost

    @Opt(names = "--regex", paramLabel = "REGEX", description = "a regex.Pattern (default: ${DEFAULT-VALUE})")
    var regex = "\\s+".r

    @Opt(names = "--url", paramLabel = "URL", description = "a regex.Pattern (default: ${DEFAULT-VALUE})")
    var url = new URL("http://google.com")

    @Opt(names = "--java-enum", paramLabel = "COLOR", description = "a Java enum value (default: ${DEFAULT-VALUE}).%nThe possible values are: ${COMPLETION-CANDIDATES}")
    var javaEnum = Colour.red

    // TODO: Find a way to automatically include the possible values for a Scala Enumeration
    @Opt(names = "--scala-enum", paramLabel = "DOW", description = "a Scala enum value (default: ${DEFAULT-VALUE}).%nThe possible values are: Mon, Tue, Wed, Thu, Fri")
    var scalaEnum: WeekDays.Value = WeekDays.Mon

    @Opt(names = "--array-int", paramLabel = "INT", split = ",", description = "a comma separated list of integers (default: ${DEFAULT-VALUE})")
    var arrayInt: Array[Int] = Array(1, 2, 3)

    @Opt(names = "--array-fraction", paramLabel = "A/B", split = ",", description = "a comma separated list of fractions (default: ${DEFAULT-VALUE})")
    var arrayFraction: Array[Fraction] = Array(Fraction(1, 2), Fraction(2, 3), Fraction(3, 4))

    @Par(arity = "1..*", paramLabel = "A/B", description = "the fractions to add")
    var fractions: Array[Fraction] = null

    override def execute(): Unit = {
      if (verbose) debug
      println(s"${fractions.toList mkString " + "} = ${fractions.foldLeft(Fraction.ZERO)(_ + _)}")
    }

    def debug: Unit =
      println(
        s"""
           |The options are:
           |* string         = ${string}
           |* int            = ${int}
           |* double         = ${double}
           |* big-dec        = ${bigDec}
           |* big-int        = ${bigInt}
           |* file           = ${file}
           |* date           = ${date}
           |* time           = ${time}
           |* datetime       = ${datetime}
           |* duration       = ${duration}
           |* period         = ${period}
           |* currency       = ${currency}
           |* inet-addr      = ${inetAddr}
           |* regex          = ${regex}
           |* url            = ${url}
           |* java-enum      = ${javaEnum}
           |* scala-enum     = ${scalaEnum}
           |* array-int      = ${arrayInt.toList}
           |* array-fraction = ${arrayFraction.toList}
           |
           |The parameters are:
           |* fractions  = ${fractions.toList}
        """.stripMargin)
  }

}

object WeekDays extends Enumeration {
  val Mon, Tue, Wed, Thu, Fri = Value
}

case class Fraction(a: Int, b: Int) {
  override def toString: String = s"${a}/${b}"

  def +(other: Fraction) = Fraction(this.a * other.b + other.a * this.b, this.b * other.b)
}

case object Fraction {
  val ZERO = Fraction(0, 1)

  def unapply(value: String): Fraction =
    value.split("/") match {
      case Array(a, b) => Fraction(a.toInt, b.toInt)
    }
}
