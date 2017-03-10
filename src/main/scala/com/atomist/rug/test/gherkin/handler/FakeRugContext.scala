package com.atomist.rug.test.gherkin.handler

import com.atomist.rug.runtime.js.RugContext
import com.atomist.rug.runtime.js.interop.jsPathExpressionEngine

/**
  * Created by rod on 3/10/17.
  */
class FakeRugContext(val teamId: String) extends RugContext {

  override val pathExpressionEngine: jsPathExpressionEngine = new jsPathExpressionEngine(this)
}
