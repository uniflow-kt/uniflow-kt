package io.uniflow.android.test

import android.arch.lifecycle.Observer
import io.uniflow.android.flow.AndroidDataFlow
import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

class SimpleObserver<T>(val callback: (T) -> Unit) : Observer<T> {
    val values = arrayListOf<T>()

    override fun onChanged(t: T?) {
        t?.let {
            values.add(t)
            callback(t)
        }
    }
}

class TestViewObserver {
    val values = arrayListOf<UIData>()
    val states = SimpleObserver<UIState> { values.add(it) }
    val events = SimpleObserver<UIEvent> { values.add(it) }

    val lastStateOrNull: UIState?
        get() = states.values.lastOrNull()
    val statesCount
        get() = states.values.count()
    val eventsCount
        get() = events.values.count()
    val lastEventOrNull
        get() = events.values.lastOrNull()
    val lastValueOrNull
        get() = values.lastOrNull()

    @Deprecated("better use verifySequence")
    fun assertReceived(vararg any: UIData) = verifySequence(*any)
    fun verifySequence(vararg testingData: UIData) {
        val testingValues = testingData.toList()
        values.forEachIndexed { index, uiData ->
            assert(uiData == testingValues[index]) {
                "Wrong value at [$index] - Type: ${uiData::class.simpleName}\nShould have [${testingValues[index]}]\nbut was [$uiData]"
            }
        }
    }
}

fun AndroidDataFlow.createTestObserver(): TestViewObserver {
    val tester = TestViewObserver()
    dataPublisher.states.observeForever(tester.states)
    dataPublisher.events.observeForever { tester.events.onChanged(it?.peek()) }
    return tester
}
