package com.atomist.rug.test.gherkin.handler.command

import com.atomist.project.archive.Rugs
import com.atomist.rug.runtime.js.RugContext
import com.atomist.rug.test.gherkin.handler.FakeRugContext
import com.atomist.rug.test.gherkin.{AbstractExecutableFeature, Definitions, FeatureDefinition, GherkinExecutionListener}
import com.atomist.source.ArtifactSource

class CommandHandlerFeature(
                      definition: FeatureDefinition,
                      definitions: Definitions,
                      rugArchive: ArtifactSource,
                      rugs: Option[Rugs],
                      listeners: Seq[GherkinExecutionListener])
  extends AbstractExecutableFeature[RugContext,CommandHandlerScenarioWorld](definition, definitions, rugs, listeners) {

  private val rugContext: RugContext = new FakeRugContext("TEST_TEAM")

  override protected def createFixture: RugContext = rugContext

  override protected def createWorldForScenario(fixture: RugContext): CommandHandlerScenarioWorld = {
    new CommandHandlerScenarioWorld(definitions, fixture, rugs)
  }
}

