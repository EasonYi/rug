package com.atomist.rug.test.gherkin.handler

import com.atomist.source.StringFileArtifact

object HandlerTestTargets {

  val Feature1 =
    """
      |Feature: Australian political history
      | This is a test
      | to demonstrate that the Gherkin DSL
      | is a good fit for Rug BDD testing
      |
      |Scenario: Australian politics, 1972-1991
      | Given a sleepy country
      | When a visionary leader enters
      | Then plan has no messages
    """.stripMargin

  val Feature1File = StringFileArtifact(
    ".atomist/test/handler/Feature1.feature",
    Feature1
  )

}
