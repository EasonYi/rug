package com.atomist.rug.runtime.js.interop

import java.util.Map.Entry
import java.util.Objects

import com.atomist.graph.GraphNode
import com.atomist.rug.spi.Typed
import com.atomist.tree.SimpleTerminalTreeNode
import jdk.nashorn.api.scripting.ScriptObjectMirror
import jdk.nashorn.internal.runtime.ConsString

/**
  * Utilities to help in binding to Nashorn.
  */
object NashornUtils {

  import scala.collection.JavaConverters._

  def extractProperties(som: ScriptObjectMirror): Map[String, Object] =
    som.entrySet().asScala.map(me => me.getKey -> me.getValue).toMap

  def toJavaType(nashornReturn: Object): Object = nashornReturn match {
    case s: ConsString => s.toString
    case r: ScriptObjectMirror if r.isArray =>
      r.values().asScala
    case x => x
  }

  def toJavaMap(nashornReturn: Object): Map[String, Object] =
    nashornReturn match {
      case som: ScriptObjectMirror =>
        val scalaMap = som.entrySet().asScala.map {
          e: Entry[String, Object] => (e.getKey, e.getValue)
        }.toMap
        scalaMap.mapValues {
          case som: ScriptObjectMirror =>
            toJavaMap(som)
          case x =>
            toJavaType(x)
        }
    }

  /**
    * Convert this object returned from Nashorn to a GraphNode if possible. Only take
    * account of properties
    */
  def toGraphNode(nashornReturn: Object): Option[GraphNode] = nashornReturn match {
    case som: ScriptObjectMirror =>
      val m = extractProperties(som)
      Some(new NashornMapBackedGraphNode(m))
    case _ =>
      None
  }

  def toScalaSeq(nashornReturn: Object): Seq[Object] = nashornReturn match {
    case r: ScriptObjectMirror if r.isArray =>
      r.values().asScala.toSeq
  }

  /**
    * Return the given property of the JavaScript object or default value if not found
    *
    * @param default default value if not found. Defaults to null.
    */
  def stringProperty(som: ScriptObjectMirror, name: String, default: String = null): String =
    som.get(name) match {
      case null => default
      case x => Objects.toString(x)
    }

  /**
    * Call the given JavaScript function, which must return a string
    */
  def stringFunction(som: ScriptObjectMirror, name: String): String =
    som.callMember(name) match {
      case null => null
      case x => Objects.toString(x)
    }

  /**
    * Are all these properties defined
    */
  def hasDefinedProperties(som: ScriptObjectMirror, properties: String*): Boolean =
    properties.forall(p => som.get(p) != null)
}

import scala.collection.JavaConverters._

/**
  * Backed by a Map that can include simple properties or Nashorn ScriptObjectMirror in the event of nesting
  */
private class NashornMapBackedGraphNode(m: Map[String, Object]) extends GraphNode {

  override def nodeName: String = m("nodeName") match {
    case None => ""
    case Some(s: String) => s
    case x => Objects.toString(x)
  }

  override def relatedNodes: Seq[GraphNode] = relatedNodeNames.flatMap(n => relatedNodesNamed(n)).toSeq

  override def relatedNodeNames: Set[String] = m.keySet -- Set("nodeName", "nodeTags")

  override def nodeTags: Set[String] = {
    m("nodeTags") match {
      case som: ScriptObjectMirror if som.isArray =>
        som.values().asScala.map(Objects.toString(_)).toSet
      case _ => Set()
    }
  }

  override def relatedNodeTypes: Set[String] = ???

  override def relatedNodesNamed(key: String): Seq[GraphNode] = m.get(key) match {
    case None => Nil
    case Some(s: String) => Seq(SimpleTerminalTreeNode(key, s, Set()))
    case x =>
      val v = Objects.toString(x)
      Seq(SimpleTerminalTreeNode(key, v, Set()))
  }
}