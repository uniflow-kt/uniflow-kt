package io.uniflow.test.impl

import io.uniflow.core.dispatcher.UniFlowDispatcher
import io.uniflow.core.flow.*
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlin.reflect.KClass

abstract class AbstractSampleFlow(defaultState: UIState) : DataFlow {
    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + supervisorJob)
    private val dataPublisher: SimpleDataPublisher = SimpleDataPublisher()
    private val dataStore: UIDataStore = UIDataStore(dataPublisher, defaultState)
    private val reducer: ActionReducer = ActionReducer(dataStore, coroutineScope, UniFlowDispatcher.dispatcher.main(), Channel.BUFFERED)
    private val actionDispatcher: ActionDispatcher
        get() = ActionDispatcher(coroutineScope, reducer, dataStore, this)

    final override fun getCurrentState() = actionDispatcher.getCurrentState()
    final override fun <T : UIState> getCurrentStateOrNull(stateClass: KClass<T>): T? = actionDispatcher.getCurrentStateOrNull()
    final override fun action(onAction: ActionFunction<UIState>): ActionFlow = actionDispatcher.action(onAction)
    final override fun action(onAction: ActionFunction<UIState>, onError: ActionErrorFunction): ActionFlow = actionDispatcher.action(onAction, onError)
    final override fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>): ActionFlow = actionDispatcher.actionOn(stateClass, onAction)
    final override fun <T : UIState> actionOn(stateClass: KClass<T>, onAction: ActionFunction<T>, onError: ActionErrorFunction): ActionFlow = actionDispatcher.actionOn(stateClass, onAction, onError)

    override suspend fun onError(error: Exception, currentState: UIState, flow: ActionFlow) {
        flow.setState { UIState.Failed("Got error $error", error) }
    }

    init {
        action { setState { defaultState } }
    }

    fun assertReceived(vararg any: UIData) {
        assert(dataPublisher.data == any.toList()) { "Wrong values\nshould have ${any.toList()}\nbut was ${dataPublisher.data}" }
    }

    fun close() {
        coroutineScope.cancel()
        reducer.close()
    }
}
