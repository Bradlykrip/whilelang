package whilelang.interpreter

import scala.jdk.CollectionConverters._
import whilelang.interpreter.Language._
import whilelang.parser.{ Antlr2Scala, WhilelangBaseListener}
import whilelang.parser.WhilelangParser._

class MyListener extends WhilelangBaseListener with Antlr2Scala[Any] {
  var program: Program = _

  override def exitProgram(ctx: ProgramContext) = 
    program = Program(ctx.seqStatement.value)

  override def exitSeqStatement(ctx: SeqStatementContext) =
    ctx.value = SeqStatement(ctx.statement.asScala.toList.map { _.value[Statement] })

  override def exitAttrib(ctx: AttribContext) =
    ctx.value = Attrib(ctx.ID.text, ctx.expression.value)

  override def exitSkip(ctx: SkipContext) =
    ctx.value = Skip

  override def exitIf(ctx: IfContext) =
    ctx.value = If(ctx.bool.value, ctx.statement(0).value, ctx.statement(1).value)

  override def exitWhile(ctx: WhileContext) =
    ctx.value = While(ctx.bool.value, ctx.statement.value)

  override def exitPrint(ctx: PrintContext) =
    ctx.value = Print(ctx.Text.text.drop(1).dropRight(1))

  override def exitWrite(ctx: WriteContext) =
    ctx.value = Write(ctx.expression.value[Expression])

  override def exitBlock(ctx: BlockContext) =
    ctx.value = ctx.seqStatement.value

  override def exitRead(ctx: ReadContext) =
    ctx.value = Read

  override def exitId(ctx: IdContext) =
    ctx.value = Id(ctx.ID.text)

  override def exitExpParen(ctx: ExpParenContext) =
    ctx.value = ctx.expression.value

  override def exitInt(ctx: IntContext) =
    ctx.value = Integer(ctx.text.toInt)

  override def exitBinOp(ctx: BinOpContext) =
    ctx.value = (ctx(1).text match {
      case "*"     => ExpMult
      case "-"     => ExpSub
      case "+" | _ => ExpSum
    })(ctx.expression(0).value, ctx.expression(1).value)

  override def exitNot(ctx: NotContext) =
    ctx.value = Not(ctx.bool.value)

  override def exitBoolean(ctx: BooleanContext) =
    ctx.value = Boole(ctx.text == "true")

  override def exitAnd(ctx: AndContext) =
    ctx.value = And(ctx.bool(0).value, ctx.bool(1).value)

  override def exitBoolParen(ctx: BoolParenContext) =
    ctx.value = ctx.bool.value

  override def exitRelOp(ctx: RelOpContext) =
    ctx.value = (ctx(1).text match {
      case "="      => ExpEqual
      case "<=" | _ => ExpLessOrEqualThan
    })(ctx.expression(0).value, ctx.expression(1).value)
}
