package com.atomist.tree.pathexpression

import com.atomist.tree.pathexpression.XPathTypes._
import com.atomist.util.lang.JavaHelpers

/**
  * Uses reflection to load functions from designated classes
  */
class ReflectiveFunctionRegistry(targets: Object*) extends FunctionRegistry {

  override val functions: Seq[Function] = {
    for {
      t <- targets
      m <- t.getClass.getMethods
      if m.getParameterCount >= 1
      if m.getParameterTypes.forall(c => c == classOf[String] || c == classOf[Boolean])
    } yield {
      val declaredArgs = m.getParameterTypes.map(_.getSimpleName) map {
        case "String" => String
        case "Boolean" => Boolean
        case wtf => throw new IllegalStateException(s"Should have filtered out parameter type [$wtf]")
      }
      // XPath functions are of the form string-length: We need to convert from Java camel case
      SimpleFunction(JavaHelpers.toLowerCaseDelimited(m.getName, "-"),
        declaredArgs,
        invoker = args => {
          //println(s"Invoking $m on $DefaultFunctions with args=${args.map(a => s"$a - ${a.getClass}")}")
          val objectified = args map {
            case o: Object => o
            case _ => ???
          }
          m.invoke(t, objectified: _*)
        })
    }
  }

  /**
    * Note that we don't need to write defensive code here as there will be the correct number of arguments
    * and they will have been coerced as to type
    */
  private case class SimpleFunction(name: String, argTypes: Seq[XPathType], invoker: Seq[Any] => Any) extends Function {

    //println(s"Creating $this")

    override def invoke(convertedArgs: Seq[Any]): Any = {
      require(convertedArgs.size == argTypes.size)
      invoker(convertedArgs)
    }
  }

}
