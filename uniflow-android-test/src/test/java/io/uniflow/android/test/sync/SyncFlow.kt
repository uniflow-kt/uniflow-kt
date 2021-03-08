package io.uniflow.android.test.sync

import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.flow.onState
import kotlinx.coroutines.delay
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
}

data class CountState(val c: Int) : UIState()