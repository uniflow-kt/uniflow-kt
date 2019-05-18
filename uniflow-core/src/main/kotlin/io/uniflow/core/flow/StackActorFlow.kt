package io.uniflow.core.flow

import io.uniflow.core.dispatcher.UniFlowDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch

abstract class StackActorFlow : StackFlow() {

    override fun executeAction(action: Action<*>) {
        flowActor.offer(action)
    }

    private val flowActor = actor<Action<*>>(UniFlowDispatcher.dispatcher.default(), capacity = 10) {
        for (action in channel) {
            GlobalScope.launch(UniFlowDispatcher.dispatcher.default()) {
                try {
                    val result = action.actionFunction.invoke(this, getCurrentState())
                    if (result is UIState) {
                        applyState(result)
                    }
                } catch (e: Throwable) {
                    handleActionError(action, e)
                }
            }
        }
    }

    override fun cancel() {
        super.cancel()
        flowActor.close()
    }
}