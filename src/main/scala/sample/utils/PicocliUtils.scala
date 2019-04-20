package sample.utils

import java.io.File
import java.time.LocalDate

import picocli.CommandLine
import picocli.CommandLine.{ITypeConverter, RunAll, Command => Cmd, Option => Opt}

import scala.util.matching.Regex

@Cmd(
  descriptionHeading = "%n",
  optionListHeading = "%nOPTIONS:%n",
  parameterListHeading = "%nPARAMETERS:%n",
  commandListHeading = "%nCOMMANDS:%n",
  usageHelpWidth = 120
)
abstract class PicocliCommand extends Runnable {
  @Opt(names = "-h, --help", usageHelp = true, description = "display this help message")
  var help = false

  override def run(): Unit = execute

  def execute(): Unit
}

abstract class PicocliSimpleCommand extends PicocliCommand

abstract class PicocliComplexCommand extends PicocliCommand

abstract class PicocliSubcommand extends PicocliCommand

class PicocliCommandLine(val cmd: PicocliCommand) extends CommandLine(cmd) {
  this.registerConverter(classOf[Regex], (value: String) => new Regex(value))

  this.registerConverter(classOf[Option[String]], (value: String) => Option(value))
  this.registerConverter(classOf[Option[Int]], (value: String) => Option(value.toInt))
  this.registerConverter(classOf[Option[Double]], (value: String) => Option(value.toDouble))
  this.registerConverter(classOf[Option[Boolean]], (value: String) => Option(value.toBoolean))
  this.registerConverter(classOf[Option[LocalDate]], (value: String) => Option(LocalDate.parse(value)))
  this.registerConverter(classOf[Option[File]], (value: String) => Option(new File(value)))

  override def registerConverter[T](clazz: Class[T], converter: ITypeConverter[T]): PicocliCommandLine = {
    super.registerConverter(clazz, converter)
    this
  }

  def run(args: Array[String]): Unit = {
    this.parseWithHandler(new RunAll, args)
  }
}
