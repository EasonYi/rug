package com.atomist.rug.test.gherkin.handler

import com.atomist.project.archive.Rugs
import com.atomist.rug.RugNotFoundException
import com.atomist.rug.runtime.{CommandHandler, EventHandler}
import com.atomist.rug.runtime.js.RugContext
import com.atomist.rug.spi.Handlers.{Message, Plan}
import com.atomist.rug.test.gherkin.{Definitions, ScenarioWorld}

class HandlerScenarioWorld(definitions: Definitions, rugContext: RugContext, rugs: Option[Rugs] = None)
  extends ScenarioWorld(definitions, rugs) {

  private var planOption: Option[Plan] = None

  /**
    * Return the editor with the given name or throw an exception
    */
  def commandHandler(name: String): CommandHandler = {
    rugs match {
      case Some(r) =>
        r.commandHandlers.find(e => e.name == name) match {
          case Some(e) => e
          case _ => throw new RugNotFoundException(
            s"CommandHandler with name '$name' can not be found in current context. Known CommandHandlers are [${r.commandHandlerNames.mkString(", ")}]")
        }
      case _ => throw new RugNotFoundException("No context provided")
    }
  }

  def eventHandler(name: String): EventHandler = {
    rugs match {
      case Some(r) =>
        r.eventHandlers.find(e => e.name == name) match {
          case Some(e) => e
          case _ => throw new RugNotFoundException(
            s"EventHandler with name '$name' can not be found in current context. Known EventHandlers are [${r.eventHandlerNames.mkString(", ")}]")
        }
      case _ => throw new RugNotFoundException("No context provided")
    }
  }

  def invokeHandler(handler: CommandHandler, params: Any): Unit = {
    planOption = handler.handle(rugContext, parameters(params))
  }

  /**
    * Return the plan or throw an exception if none was recorded
    */
  def plan: jsPlan =
    planOption.map(new jsPlan(_)).getOrElse(throw new IllegalArgumentException("No plan was recorded"))


}

import scala.collection.JavaConverters._

/**
  * JavaScript-friendly version of Plan structure, without Scala collections and using null instead of Option
  */
class jsPlan(plan: Plan) {

  def messages: java.util.List[jsMessage] =
    plan.messages.map(new jsMessage(_)).asJava

}

class jsMessage(message: Message) {

  def body = message.body

}
