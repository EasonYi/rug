package com.atomist.rug.spi

import com.atomist.param.ParameterValues
import com.atomist.rug.spi.Handlers.Response

/**
  * Arbitrary functions to be executed as a result add 'execute' instructions to a Plan
  * Should be thread safe.
  *
  * Secrets should be injected as normal ParameterValues
  */
trait RugFunction extends SecretAwareRug {
  /**
    * Run the function, return the Response
    * @param parameters
    * @return
    */
  def run(parameters: ParameterValues): Response
}
