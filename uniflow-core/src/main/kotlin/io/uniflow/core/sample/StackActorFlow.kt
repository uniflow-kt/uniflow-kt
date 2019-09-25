package io.uniflow.core.sample

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.StateAction
import io.uniflow.core.flow.onIO
import kotlinx.coroutines.channels.actor

abstract class StackActorFlow : StackFlow() {

    override fun onAction(action: StateAction) {
        flowActor.offer(action)
    }

    private val flowActor = actor<StateAction>(UniFlowDispatcher.dispatcher.default(), capacity = 10) {
        for (action in channel) {
            onIO {
                proceedAction(action)
            }
        }
    }

    override fun cancel() {
        super.cancel()
        flowActor.close()
    }
}