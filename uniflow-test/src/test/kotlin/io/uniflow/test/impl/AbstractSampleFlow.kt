package io.uniflow.test.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.ActionDispatcher
import io.uniflow.core.flow.ActionReducer
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.DataPublisher
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel

abstract class AbstractSampleFlow(val defaultState: UIState) : DataFlow, DataPublisher {
    override val tag = this.toString()

    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    override val defaultDataPublisher = defaultPublisher(defaultState)
    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) = defaultDataPublisher.publishState(state, pushStateUpdate)
    override suspend fun publishEvent(event: UIEvent) = defaultDataPublisher.publishEvent(event)
    override suspend fun getState(): UIState = defaultDataPublisher.getState()

    private val actionReducer = ActionReducer(defaultDataPublisher, coroutineScope, UniFlowDispatcher.dispatcher.main(), Channel.BUFFERED, tag)
    override val actionDispatcher: ActionDispatcher = ActionDispatcher(coroutineScope, actionReducer, ::onError, tag)

    override suspend fun onError(error: Exception, currentState: UIState) {
        action { setState { UIState.Failed(error = error) } }
    }

    fun assertReceived(vararg any: UIData) {
        assert(defaultDataPublisher.data == any.toList()) { "Wrong values\nshould have ${any.toList()}\nbut was ${defaultDataPublisher.data}" }
    }

    fun close() {
        coroutineScope.cancel()
        actionDispatcher.close()
    }
}

fun defaultPublisher(defaultState: UIState, tag: String = "default") = SimpleDataPublisher(defaultState,tag)
