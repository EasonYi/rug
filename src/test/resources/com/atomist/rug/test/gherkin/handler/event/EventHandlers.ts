import {HandleEvent, Plan, Message} from '@atomist/rug/operations/Handlers'
import {TreeNode, Match, PathExpression} from '@atomist/rug/tree/PathExpression'
import {EventHandler, Tags} from '@atomist/rug/operations/Decorators'

@EventHandler("ReturnsEmptyPlanEventHandler", "Handles a Build event", new PathExpression<TreeNode,TreeNode>("/build"))
@Tags("github", "build")
class ReturnsEmptyPlanEventHandler implements HandleEvent<TreeNode,TreeNode> {

  handle(event: Match<TreeNode, TreeNode>){
    //let issue = event.root
    return new Plan();
  }
}
export let handler = new ReturnsEmptyPlanEventHandler();