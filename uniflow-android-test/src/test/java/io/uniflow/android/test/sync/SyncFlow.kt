package io.uniflow.android.test.sync

import androidx.lifecycle.viewModelScope
import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SyncFlow : AndroidDataFlow(UIState.Empty) {

    fun action1() = action {
        val d = Random.nextLong(100)
        println("delay: $d")
        delay(d)
        setState { CountState(1) }
    }

    fun action2() = action {
        val d = Random.nextLong(100)
        println("delay: $d")
        delay(d)
        setState { CountState(2) }
    }

    fun action3() = action {
        val d = Random.nextLong(100)
        println("delay: $d")
        delay(d)
        setState { CountState(3) }
    }

    fun actionList() {
        viewModelScope.launch {
            flow { (1..3).forEach { emit(it) } }
                .collect { value ->
                    action {
                        setState { CountState(value) }
                    }
                }
        }
    }
}

data class CountState(val c: Int) : UIState()
