package com.atomist.rug.test.gherkin.handler.event

import com.atomist.project.archive.{AtomistConfig, DefaultAtomistConfig, RugArchiveReader}
import com.atomist.rug.TestUtils._
import com.atomist.rug.runtime.js.JavaScriptContext
import com.atomist.rug.test.gherkin.{GherkinRunner, Passed}
import com.atomist.rug.ts.TypeScriptBuilder
import com.atomist.source.{ArtifactSourceUtils, SimpleFileBasedArtifactSource, StringFileArtifact}
import org.scalatest.{FlatSpec, Matchers}

class GherkinRunnerEventHandlerTest extends FlatSpec with Matchers {

  import EventHandlerTestTargets._

  val atomistConfig: AtomistConfig = DefaultAtomistConfig

  "event handler testing" should "verify no plan steps" in {
    val passingFeature1StepsFile = requiredFileInPackage(
      this,
      "PassingFeature1Steps.ts",
      ".atomist/test/handler/PassingFeature1Steps.ts"
    )

    val handlerName = "ReturnsEmptyPlanEventHandler.ts"
    val handlerFile = requiredFileInPackage(this, "EventHandlers.ts", atomistConfig.handlersRoot + "/event/" + handlerName)
    val as = SimpleFileBasedArtifactSource(Feature1File, passingFeature1StepsFile, handlerFile)

    val cas = TypeScriptBuilder.compileWithModel(as)
    val grt = new GherkinRunner(new JavaScriptContext(cas), Some(RugArchiveReader.find(cas)))
    val run = grt.execute()
    //println(new TestReport(run))
    run.result match {
      case Passed =>
      case wtf => fail(s"Unexpected: $wtf")
    }
  }
}
