package sample

import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.{ParentCommand, Spec, Command => Cmd, Option => Opt, Parameters => Par}
import sample.utils.{PicocliCommandLine, PicocliComplexCommand, PicocliSubcommand}

object ComplexApp extends App {
  new PicocliCommandLine(new Command).run(args)

  @Cmd(name = "complex-app", description = "A complex app with sub-commands.", subcommands = Array(
    classOf[SubcommandAdd],
    classOf[SubcommandSub],
    classOf[SubcommandMul],
    classOf[SubcommandDiv],
  ))
  class Command extends PicocliComplexCommand {
    @Opt(names = "-v, --verbose", description = "print more info")
    var verbose = false

    override def execute(): Unit = {
      if (verbose) println(s"Running main command with verbose = ${verbose}...")
    }
  }

  abstract class BinOpSubcommand extends PicocliSubcommand {
    @Spec
    var spec: CommandSpec = null

    @ParentCommand
    var parent: Command = null

    @Par(index = "0", paramLabel = "NUM1", description = "the left operand")
    var a = 0.0

    @Par(index = "1", paramLabel = "NUM2", description = "the right operand")
    var b = 0.0

    override def execute(): Unit = {
      if (parent.verbose) println(s"Running ${spec.name} with a = ${a} and b = ${b}")
      println(calculate)
    }

    def calculate: Double
  }

  @Cmd(name = "add", description = "Calculate the sum of two numbers.")
  class SubcommandAdd extends BinOpSubcommand {
    override def calculate: Double = a + b
  }

  @Cmd(name = "sub", aliases = "subtract", description = "Calculate the difference of two numbers.")
  class SubcommandSub extends BinOpSubcommand {
    override def calculate: Double = a - b
  }

  @Cmd(name = "mul", aliases = "multiply", description = "Calculate the product of two numbers.")
  class SubcommandMul extends BinOpSubcommand {
    override def calculate: Double = a * b
  }

  @Cmd(name = "div", aliases = "divide", description = "Calculate the quotient of two numbers.")
  class SubcommandDiv extends BinOpSubcommand {
    override def calculate: Double = a / b
  }

}
