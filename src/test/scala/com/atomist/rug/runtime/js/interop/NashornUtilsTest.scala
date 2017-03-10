package com.atomist.rug.runtime.js.interop

import com.atomist.tree.TreeNode
import jdk.nashorn.api.scripting.{NashornScriptEngine, NashornScriptEngineFactory}
import org.scalatest.{FlatSpec, Matchers}

class NashornUtilsTest extends FlatSpec with Matchers {

  val engine: NashornScriptEngine =
    new NashornScriptEngineFactory()
      .getScriptEngine("--optimistic-types", "--language=es6", "--no-java")
      .asInstanceOf[NashornScriptEngine]

  import NashornUtils._

  "toGraphNode" should "fail to convert null without error" in {
    toGraphNode(null, "test") shouldBe empty
  }

  it should "fail to convert non Nashorn return without error" in {
    toGraphNode(new Object(), "test") shouldBe empty
  }

  it should "get name and properties from simple node" in {
    val n = engine.eval(
      """
        |{
        |   var x = { nodeName: 'Gangster', forename: 'Johnny', surname: 'Caspar', nodeTags: ["tag1", "tag2"]};
        |   x
        |}
      """.stripMargin
    )
    val gn = toGraphNode(n).get
    assert(gn.nodeName === "Gangster")
    assert(gn.relatedNodeNames === Set("forename", "surname"))
    assert(gn.relatedNodesNamed("surname").size === 1)
    val forename = gn.relatedNodesNamed("surname").head
    assert(forename.asInstanceOf[TreeNode].value === "Caspar")
    assert(gn.relatedNodes.size === 2)
  }

  it should "get tags from simple node" in {
    val n = engine.eval(
      """
        |{
        |   var x = { nodeName: 'Gangster', forename: 'Johnny', surname: 'Caspar', nodeTags: ["tag1", "tag2"]};
        |   x
        |}
      """.stripMargin
    )
    val gn = toGraphNode(n).get
    assert(gn.nodeTags === Set("tag1", "tag2"))
  }

  it should "get name and properties from nested node" in pendingUntilFixed {
    val rootName = "Gangster"
    val n = engine.eval(
      s"""
        |{
        |   var x = { nodeName: '$rootName', forename: 'Johnny', surname: 'Caspar',
        |     associate: { nodeName: 'Leo', forename: 'Leo', nodeTags: ["Irish"]},
        |     nodeTags: ["tag1", "tag2"]};
        |   x
        |}
      """.stripMargin
    )
    val gn = toGraphNode(n).get
    assert(gn.nodeName === rootName)
    assert(gn.relatedNodeNames === Set("forename", "surname", "associate"))
    assert(gn.relatedNodesNamed("surname").size === 1)
    val forename = gn.relatedNodesNamed("surname").head
    assert(forename.asInstanceOf[TreeNode].value === "Caspar")
    assert(gn.relatedNodesNamed("associate").size === 1)
    val assoc = gn.relatedNodesNamed("associate").head
    assert(assoc.nodeName === "Leo")
    assert(assoc.nodeTags.contains("Irish"))
    assert(assoc.relatedNodesNamed("forename").head.asInstanceOf[TreeNode].value === "Leo")
  }

}
