package com.atomist.rug.spi

import com.atomist.rug.kind.dynamic.ViewFinder
import com.atomist.rug.runtime.Evaluator

/**
  * Support for a new Rug "kind" or "type" that can be used in with or from comprehensions, such
  * as a Java class or Elm module.
  * When kinds are nested, the context should be the mutable view of
  * the outer kind.
  *
  * @param evaluator used to evaluate expressions
  */
abstract class Type(
                     evaluator: Evaluator
                   )
  extends ViewFinder with Typed {

  override def underlyingType: Class[_] = viewManifest.runtimeClass

  /** Describe the MutableView subclass to allow for reflective function export */
  def viewManifest: Manifest[_]

}
