package io.uniflow.android.test.sync

import androidx.lifecycle.viewModelScope
import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SyncFlow : AndroidDataFlow(UIState.Empty) {

    fun action1(d: Long = getRandomDelay()) = action {
        println("delay: $d")
        delay(d)
        setState { CountState(1) }
    }

    fun action2(d: Long = getRandomDelay()) = action {
        println("delay: $d")
        delay(d)
        setState { CountState(2) }
    }

    fun action3(d: Long = getRandomDelay()) = action {
        println("delay: $d")
        delay(d)
        setState { CountState(3) }
    }

    fun getRandomDelay(): Long {
        return Random.nextLong(50)
    }

    fun actionBoom() = action {
        error("boom")
    }

    fun actionList() {
        viewModelScope.launch {
            val flow = flow { (1..3).forEach { emit(it) } }
            flow.collect { value ->
                action {
                    setState { CountState(value) }
                }
            }
        }
    }

    fun clearState() = action {
        notifyStateUpdate(
            CountState(0),
            ClearStateEvent
        )
    }

    override suspend fun onError(error: Exception, currentState: UIState) {
        setState { UIState.Failed(error = error) }
    }
}

data class CountState(val c: Int) : UIState()
object ClearStateEvent : UIEvent()
