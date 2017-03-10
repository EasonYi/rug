package com.atomist.rug.test.gherkin.handler.command

import com.atomist.project.archive.Rugs
import com.atomist.rug.runtime.CommandHandler
import com.atomist.rug.runtime.js.RugContext
import com.atomist.rug.test.gherkin.Definitions
import com.atomist.rug.test.gherkin.handler.AbstractHandlerScenarioWorld

class CommandHandlerScenarioWorld(definitions: Definitions, rugContext: RugContext, rugs: Option[Rugs] = None)
  extends AbstractHandlerScenarioWorld(definitions, rugContext, rugs) {

  def invokeHandler(handler: CommandHandler, params: Any): Unit = {
    recordPlan(handler.handle(rugContext, parameters(params)))
  }

}

