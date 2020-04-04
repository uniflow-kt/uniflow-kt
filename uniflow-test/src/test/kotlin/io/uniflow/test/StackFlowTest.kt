package io.uniflow.test

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.DebugMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.test.data.Todo
import io.uniflow.test.data.TodoListState
import io.uniflow.test.data.TodoRepository
import io.uniflow.test.impl.SampleFlow
import io.uniflow.test.rule.TestDispatchersRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StackFlowTest {

    init {
        UniFlowLogger.init(DebugMessageLogger())
    }

    @get:Rule
    var rule = TestDispatchersRule()

    val repository = TodoRepository()
    lateinit var dataFlow: SampleFlow

    @Before
    fun before() {
        dataFlow = SampleFlow(repository)
    }

    @Test
    fun `empty state`() {
        dataFlow.hasStates(UIState.Empty)
        dataFlow.hasNoEvent()
    }

    @Test
    fun `get all`() {
        dataFlow.getAll()
        dataFlow.hasStates(UIState.Empty, TodoListState(emptyList()))
        dataFlow.hasNoEvent()
    }

    @Test
    fun `add one`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.hasStates(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))))
        dataFlow.hasNoEvent()
    }

    @Test
    fun `add one - fail`() {
        dataFlow.add("first")

        dataFlow.hasStates(UIState.Empty)
        dataFlow.hasEvents(UIEvent.BadOrWrongState(UIState.Empty))
    }

    @Test
    fun `done`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.done("first")

        dataFlow.hasStates(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                TodoListState(listOf(Todo("first", true))))
        dataFlow.hasNoEvent()
    }

    @Test
    fun `filter dones`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.add("second")
        dataFlow.done("first")
        dataFlow.filterDones()

        dataFlow.hasStates(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                TodoListState(listOf(Todo("first"), Todo("second"))),
                TodoListState(listOf(Todo("second"), Todo("first", true))),
                TodoListState(listOf(Todo("first", true))))
        dataFlow.hasNoEvent()
    }

    @Test
    fun `done - fail`() {
        dataFlow.getAll()
        dataFlow.done("first")

        dataFlow.hasStates(
                UIState.Empty,
                TodoListState(emptyList()))
        dataFlow.hasEvents(UIEvent.Fail(message = "Can't make done 'first'"))
    }

    @Test
    fun `action error`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.makeOnError()

        dataFlow.hasStates(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))))
        assert(dataFlow.lastEvent is UIEvent.Fail)
    }

    @Test
    fun `global action error`() = runBlocking {
        dataFlow.makeGlobalError()
        delay(100)

        dataFlow.hasState(0,UIState.Empty)
        assert(dataFlow.lastState is UIState.Failed)
        dataFlow.hasNoEvent()
    }

    @Test
    fun `child io action error`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.childIOError()

        dataFlow.hasState(0,UIState.Empty)
        dataFlow.hasState(1,TodoListState(emptyList()))
        dataFlow.hasState(2,TodoListState(listOf(Todo("first"))))
        assert(dataFlow.lastState is UIState.Failed)
        dataFlow.hasNoEvent()
    }

    @Test
    fun `child io action`() = runBlocking {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.childIO()
        delay(200)

        dataFlow.hasStates(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                TodoListState(listOf(Todo("first"), Todo("LongTodo")))
        )
        dataFlow.hasNoEvent()
    }

    @Test
    fun `cancel test`() = runBlocking {
        dataFlow.getAll()
        dataFlow.longWait()
        delay(300)
        dataFlow.close()

        dataFlow.hasStates(
                UIState.Empty,
                TodoListState(emptyList())
        )
        dataFlow.hasNoEvent()
    }

    @Test
    fun `cancel before test`() = runBlocking {
        dataFlow.getAll()
        dataFlow.close()
        dataFlow.longWait()

        dataFlow.hasStates(
                UIState.Empty,
                TodoListState(emptyList())
        )
        dataFlow.hasNoEvent()
    }

}