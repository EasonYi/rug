package com.atomist.rug.test.gherkin.handler.event

import com.atomist.project.archive.Rugs
import com.atomist.rug.RugNotFoundException
import com.atomist.rug.runtime.EventHandler
import com.atomist.rug.runtime.js.RugContext
import com.atomist.rug.test.gherkin.Definitions
import com.atomist.rug.test.gherkin.handler.AbstractHandlerScenarioWorld

class EventHandlerScenarioWorld(definitions: Definitions, rugContext: RugContext, rugs: Option[Rugs] = None)
  extends AbstractHandlerScenarioWorld(definitions, rugContext, rugs) {

  //private var registeredHandlers: Set[EventHandler] = Set()

  private var handler: Option[EventHandler] = _

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

  def registerHandler(name: String): EventHandler = {
    val eh = eventHandler(name)
    handler = Some(eh)
    eh
  }

  def sendEvent(e: Any): Unit = {
    println(s"Received event [$e]")
    val h = handler.getOrElse(throw new IllegalStateException("No handler is registered"))
    //if (h.rootNodeName == )
  }

}

