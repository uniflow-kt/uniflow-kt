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
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    internal val defaultDataPublisher = simpleListPublisher(defaultState, "main")
    override suspend fun publishState(state: UIState, pushStateUpdate: Boolean) = defaultPublisher().publishState(state, pushStateUpdate)
    override suspend fun publishEvent(event: UIEvent) = defaultPublisher().publishEvent(event)
    override suspend fun getState(): UIState = defaultPublisher().getState()
    override fun defaultPublisher(): DataPublisher = defaultDataPublisher

    private val actionReducer = ActionReducer(::defaultPublisher, coroutineScope, UniFlowDispatcher.dispatcher.main(), Channel.BUFFERED, tag)
    override val actionDispatcher: ActionDispatcher = ActionDispatcher(actionReducer, ::onError, tag)

    fun assertReceived(vararg any: UIData) {
        assert(defaultDataPublisher.data == any.toList()) { "Wrong values\nshould have ${any.toList()}\nbut was ${defaultDataPublisher.data}" }
    }

    fun close() {
        coroutineScope.cancel()
        actionDispatcher.close()
    }
}

fun simpleListPublisher(defaultState: UIState, tag: String) = SimpleDataPublisher(defaultState, tag)
