package io.uniflow.test.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.ActionDispatcher
import io.uniflow.core.flow.ActionReducer
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.DataPublisher
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel

abstract class AbstractSampleFlow(defaultState: UIState) : DataFlow, DataPublisher by defaultPublisher(defaultState) {
    override val tag = this.toString()

    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    private val actionReducer = ActionReducer(this, coroutineScope, UniFlowDispatcher.dispatcher.main(), Channel.BUFFERED, tag)
    override val actionDispatcher: ActionDispatcher = ActionDispatcher(coroutineScope, actionReducer, ::onError, tag)

    init {
        action { setState(defaultState) }
    }

    override suspend fun onError(error: Exception, currentState: UIState) {
        action { setState { UIState.Failed("Got error $error", error) } }
    }

    fun assertReceived(vararg any: UIData) {
//        val sdp = this as SimpleDataPublisher
//        assert(sdp.data == any.toList()) { "Wrong values\nshould have ${any.toList()}\nbut was ${sdp.data}" }
    }

    fun close() {
        coroutineScope.cancel()
        actionDispatcher.close()
    }
}

fun defaultPublisher(defaultState: UIState): DataPublisher = SimpleDataPublisher(defaultState)
