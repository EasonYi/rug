import {Given,When,Then, HandlerScenarioWorld} from "@atomist/rug/test/handler/Core"

Given("a sleepy country", f => {
})
When("a visionary leader enters", (rugContext, world) => {
   world.registerHandler("ReturnsEmptyPlanEventHandler")
   world.sendEvent(//JSON.stringify(
       {
       nodeName: "er",
       nodeTags: []
   }
   //)
   )
})
Then("excitement ensues", (p,world) => {
return false
    //return world.plan().messages().length == 0
})