
/**
   Returned when a PathExpression is evaluated
*/
class Match<R,N> {

  private _root: R;
  private _matches: Array<N>;

  constructor(root: R, matches: Array<N>) {
    this._root = root
    this._matches = matches;
  }

  root(): R { return this._root; }
  matches(): Array<N> { return this._matches;}
}

class PathExpression<R,N> {

  constructor(public expression: string) {}

}

/**
 * All tree nodes offer these basic operations.
 */
interface TreeNode {

  nodeName(): String

  nodeType(): String

  value(): String

  update(newValue: String)

}

/*
  What we use to execute tree expressions
*/
interface PathExpressionEngine {

  evaluate<R extends TreeNode,N extends TreeNode>(root, expr: PathExpression<R,N>): Match<R,N>

/**
 * Return a single match. Throw an exception otherwise.
 */
  scalar<R extends TreeNode,N extends TreeNode>(root, expr: PathExpression<R,N>): N

// cast the current node
  as<N extends TreeNode>(root, name: string): N

  // Find the children of the current node of this time
  children<N extends TreeNode>(root, name: string): Array<N>
}

export {Match}
export {PathExpression}
export {PathExpressionEngine}
export {TreeNode}