package io.uniflow.core.flow.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.Action
import io.uniflow.core.flow.UIState
import io.uniflow.core.flow.onIO
import kotlinx.coroutines.channels.actor

abstract class StackActorFlow : StackFlow() {

    override fun onAction(action: Action<UIState?, *>) {
        flowActor.offer(action)
    }

    private val flowActor = actor<Action<UIState?, *>>(UniFlowDispatcher.dispatcher.default(), capacity = 10) {
        for (action in channel) {
            onIO {
                try {
                    val result = action.actionFunction.invoke(this, getCurrentState())
                    if (result is UIState) {
                        applyState(result)
                    }
                } catch (e: Throwable) {
                    onError(action, e)
                }
            }
        }
    }

    override fun cancel() {
        super.cancel()
        flowActor.close()
    }
}