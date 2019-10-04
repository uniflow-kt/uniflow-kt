package io.uniflow.core.sample

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.ActorFlow
import io.uniflow.core.flow.StateAction
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.threading.onIO
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.isActive

abstract class StackActorFlow : StackFlow(), ActorFlow {

    override val actorFlow = coroutineScope.actor<StateAction>(UniFlowDispatcher.dispatcher.default(), capacity = 10) {
        for (action in channel) {
            if (coroutineScope.isActive) {
                UniFlowLogger.log("StackActorFlow run action $action")
                onIO {
                    proceedAction(action)
                }
            } else {
                UniFlowLogger.log("StackActorFlow action cancelled")
            }
        }
    }

    override fun cancel() {
        super.cancel()
        actorFlow.close()
    }
}