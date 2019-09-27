package io.uniflow.core.flow

class StateFlowPublisher(val flow: DataFlow, val onError: ErrorFunction? = null) {
    suspend fun setState(state: UIState) {
        flow.proceedAction(Action<UIState?, UIState>({ state }, onError))
    }
}