package sample

import picocli.CommandLine.{Command => Cmd, Option => Opt, Parameters => Par}
import sample.utils.{PicocliCommandLine, PicocliSimpleCommand}

case class Person(name: String)

object SimpleApp extends App {
  new PicocliCommandLine(new Command).
    registerConverter(classOf[Person], p => Person(p.capitalize)).
    run(args)

  @Cmd(name = "simple-app", description = "A simple app with a single command.")
  class Command extends PicocliSimpleCommand {
    @Opt(names = "-s, --sep, --print-seps, --use-seps", description = "print the separators")
    var seps = false

    @Opt(names = "-c, --sep-char", paramLabel = "CHAR", description = "the character to use for the separators (default: ${DEFAULT-VALUE})")
    var char = '='

    @Opt(names = "-w, --sep-width", paramLabel = "NUM", description = "the separators width (default: ${DEFAULT-VALUE})")
    var width = 60

    @Opt(names = "--prolog", paramLabel = "TEXT", description = "the text to print at the beginning")
    var prolog: Option[String] = None

    @Opt(names = "--epilog", paramLabel = "TEXT", description = "the text to print at the end")
    var epilog: Option[String] = None

    @Par(arity = "1..*", paramLabel = "NAME", description = "the name of persons to greet")
    var persons: Array[Person] = null

    override def execute(): Unit = {
      val sep = if (seps) Some(char.toString * width) else None
      sep foreach println
      prolog foreach println
      persons.map(p => s"Hello, ${p}!") foreach println
      epilog foreach println
      sep foreach println
    }
  }

}
