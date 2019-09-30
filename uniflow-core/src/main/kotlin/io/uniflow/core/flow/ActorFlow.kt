package io.uniflow.core.flow

import io.uniflow.core.logger.UniFlowLogger
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.isActive

interface ActorFlow : DataFlow {


    override fun onAction(action: StateAction) {
        coroutineScope.apply {
            if (isActive) {
                UniFlowLogger.log("ActorFlow.onAction offer action $action")
                actorFlow.offer(action)
            } else {
                UniFlowLogger.log("ActorFlow.onAction offer action cancelled")
            }
        }
    }

    val actorFlow: SendChannel<StateAction>

}