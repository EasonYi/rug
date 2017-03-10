package com.atomist.rug.test.gherkin.handler

import com.atomist.project.archive.{AtomistConfig, DefaultAtomistConfig, RugArchiveReader}
import com.atomist.rug.TestUtils._
import com.atomist.rug.runtime.js.JavaScriptContext
import com.atomist.rug.test.gherkin.{GherkinRunner, Passed}
import com.atomist.rug.ts.TypeScriptBuilder
import com.atomist.source.{ArtifactSourceUtils, SimpleFileBasedArtifactSource, StringFileArtifact}
import org.scalatest.{FlatSpec, Matchers}

class GherkinRunnerAgainstHandlersTest extends FlatSpec with Matchers {

  import HandlerTestTargets._

  val atomistConfig: AtomistConfig = DefaultAtomistConfig

  "handler testing" should "perform simple steps" in {
    val passingFeature1Steps =
      """
        |import {Given,When,Then, HandlerScenarioWorld} from "@atomist/rug/test/handler/Core"
        |
        |Given("a sleepy country", f => {
        | console.log("Given invoked for handler")
        |})
        |When("a visionary leader enters", (rugContext, world) => {
        |   let handler = world.commandHandler("ReturnsEmptyPlanCommandHandler")
        |   world.invokeHandler(handler)
        |})
        |Then("excitement ensues", p => true)
      """.stripMargin
    val passingFeature1StepsFile = StringFileArtifact(
      ".atomist/test/handler/PassingFeature1Step.ts",
      passingFeature1Steps
    )

    val handlerName = "ReturnsEmptyPlanCommandHandler.ts"
    val handlerFile = requiredFileInPackage(this, handlerName).withPath(atomistConfig.handlersRoot + "/" + handlerName)
    val as = SimpleFileBasedArtifactSource(Feature1File, passingFeature1StepsFile, handlerFile)

    println(ArtifactSourceUtils.prettyListFiles(as))

    val cas = TypeScriptBuilder.compileWithModel(as)
    val grt = new GherkinRunner(new JavaScriptContext(cas), Some(RugArchiveReader.find(as)))
    val run = grt.execute()
    //println(new TestReport(run))
    run.result match {
      case Passed =>
      case wtf => fail(s"Unexpected: $wtf")
    }
  }
}
