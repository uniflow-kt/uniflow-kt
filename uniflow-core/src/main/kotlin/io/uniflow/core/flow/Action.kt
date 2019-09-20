package io.uniflow.core.flow

data class Action(val stateFunction: StateFunction<*>? = null, val errorFunction: ErrorFunction? = null) {

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun setState(onStateUpdate: StateUpdateFunction, onError: ErrorFunction) {
        error("")
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun setState(updateFunction: StateUpdateFunction) {
        error("")
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun withState(onStateAction: StateActionFunction, errorFunction: ErrorFunction) {
        error("")
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun withState(onStateAction: StateActionFunction) {
        error("")
    }

    @Deprecated("", level = DeprecationLevel.ERROR)
    fun applyState(state: UIState){
        error("")
    }
}

typealias StateFunction<T> = suspend Action.(UIState?) -> T
typealias StateUpdateFunction = StateFunction<UIState?>
typealias StateActionFunction = StateFunction<Unit>
typealias ErrorFunction = suspend Action.(Exception) -> UIState?