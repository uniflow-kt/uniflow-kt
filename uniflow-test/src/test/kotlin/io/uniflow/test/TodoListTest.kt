package io.uniflow.test

import io.uniflow.core.flow.UIState
import io.uniflow.core.logger.SimpleMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.test.rule.UniFlowDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TodoListTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesMainDispatcherRule = UniFlowDispatcherRule()

    val repository = MyTodoRepository()
    lateinit var dataFlow: MyTodoListFlow

    @Before
    fun before() {
        dataFlow = MyTodoListFlow(repository)
        UniFlowLogger.init(SimpleMessageLogger())

    }

    @Test
    fun `empty state`() {
        assertEquals(UIState.Empty, dataFlow.states.first())
    }
}