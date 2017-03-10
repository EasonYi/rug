package com.atomist.rug.runtime.js.interop

import java.util.Objects

import com.atomist.graph.GraphNode
import com.atomist.tree.SimpleTerminalTreeNode
import jdk.nashorn.api.scripting.ScriptObjectMirror

import scala.collection.JavaConverters._

object NashornMapBackedGraphNode {

  /**
    * Convert this object returned from Nashorn to a GraphNode if possible. Only take
    * account of properties
    */
  def toGraphNode(nashornReturn: Object): Option[GraphNode] = nashornReturn match {
    case som: ScriptObjectMirror =>
      Some(new NashornMapBackedGraphNode(som))
    case _ =>
      None
  }
}

/**
  * Backed by a Map that can include simple properties or Nashorn ScriptObjectMirror in the event of nesting
  */
private class NashornMapBackedGraphNode(som: ScriptObjectMirror) extends GraphNode {

  private val m = NashornUtils.extractProperties(som)

  override def nodeName: String = m.get("nodeName") match {
    case None => ""
    case Some(s: String) => s
    case x => Objects.toString(x)
  }

  override def nodeTags: Set[String] = {
    m("nodeTags") match {
      case som: ScriptObjectMirror if som.isArray =>
        som.values().asScala.map(Objects.toString(_)).toSet
      case _ => Set()
    }
  }

  override lazy val relatedNodes: Seq[GraphNode] =
    m.keySet.filter(!Set("nodeName", "nodeTags").contains(_)).flatMap(toNode).toSeq

  override lazy val relatedNodeNames: Set[String] = relatedNodes.map(_.nodeName).toSet

  override def relatedNodeTypes: Set[String] = relatedNodes.flatMap(_.nodeTags).toSet

  override def relatedNodesNamed(name: String): Seq[GraphNode] =
    relatedNodes.filter(_.nodeName == name)

  override def followEdge(name: String): Seq[GraphNode] =
    toNode(name)

  private def toNode(key: String): Seq[GraphNode] = m.get(key) match {
    case None => Nil
    case Some(s: String) => Seq(SimpleTerminalTreeNode(key, s, Set()))
    case Some(som: ScriptObjectMirror) if som.isArray =>
      som.values().asScala.flatMap(NashornMapBackedGraphNode.toGraphNode).toSeq
    case Some(som: ScriptObjectMirror) =>
      Seq(new NashornMapBackedGraphNode(som))
    case x =>
      val v = Objects.toString(x)
      Seq(SimpleTerminalTreeNode(key, v, Set()))
  }

}
