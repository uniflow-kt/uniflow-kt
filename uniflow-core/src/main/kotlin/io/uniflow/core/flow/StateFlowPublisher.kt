package io.uniflow.core.flow

class StateFlowPublisher(val flow: DataFlow, val onError: ErrorFunction? = null) {
    suspend fun setState(state: UIState) {
        val action: Action<UIState?, UIState> = if (onError != null) {
            Action({ state }, onError)
        } else Action({ state })
        flow.proceedAction(action)
    }
}