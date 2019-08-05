package io.uniflow.test.flow

import io.uniflow.core.flow.UIState
import io.uniflow.core.logger.SimpleMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.logger.UniFlowLogger.FUN_TAG
import io.uniflow.test.data.Todo
import io.uniflow.test.data.TodoListState
import io.uniflow.test.data.TodoRepository
import io.uniflow.test.data.TodoStackFlow
import io.uniflow.test.rule.TestThreadRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StackFlowThreadedTest {

    init {
        UniFlowLogger.init(SimpleMessageLogger(FUN_TAG, debugThread = true))
    }

    @get:Rule
    var coroutinesMainDispatcherRule = TestThreadRule()

    val repository = TodoRepository()
    lateinit var dataFlow: TodoStackFlow

    @Before
    fun before() {
        dataFlow = TodoStackFlow(repository)
    }

    @Test
    fun `empty state`() = runBlocking {
        delay(10)
        assertEquals(UIState.Empty, dataFlow.states.first())
    }

    @Test
    fun `get all`() = runBlocking {
        dataFlow.getAll()
        delay(10)
        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
    }

    @Test
    fun `add one`() = runBlocking {
        dataFlow.getAll()
        delay(10)
        dataFlow.add("first")
        delay(10)

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
    }

    @Test
    fun `stateflow test`() = runBlocking {
        dataFlow.flow()
        delay(10)

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(UIState.Empty, dataFlow.states[1])
        assertEquals(UIState.Loading, dataFlow.states[2])
        assertEquals(UIState.Success, dataFlow.states[3])

        Assert.assertTrue(dataFlow.states.size == 4)
        Assert.assertTrue(dataFlow.events.size == 0)
    }

//    @Test
//    fun `add one - fail`() {
//        dataFlow.add("first")
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//
//        assertTrue(dataFlow.events[0] is UIEvent.Fail)
//    }
//
//    @Test
//    fun `done`() {
//        dataFlow.getAll()
//        dataFlow.add("first")
//        dataFlow.done("first")
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
//        assertEquals(TodoListState(listOf(Todo("first", true))), dataFlow.states[3])
//    }
//
//    @Test
//    fun `filter dones`() {
//        dataFlow.getAll()
//        dataFlow.add("first")
//        dataFlow.add("second")
//        dataFlow.done("first")
//        dataFlow.filterDones()
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
//        assertEquals(TodoListState(listOf(Todo("first"), Todo("second"))), dataFlow.states[3])
//        assertEquals(TodoListState(listOf(Todo("second"), Todo("first", true))), dataFlow.states[4])
//        assertEquals(TodoListState(listOf(Todo("first", true))), dataFlow.states[5])
//    }
//
//    @Test
//    fun `done - fail`() {
//        dataFlow.getAll()
//        dataFlow.done("first")
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//
//        assertTrue(dataFlow.events[0] is UIEvent.Fail)
//    }
//
//    @Test
//    fun `action flowError`() {
//        dataFlow.getAll()
//        dataFlow.add("first")
//        dataFlow.makeOnError()
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
//
//        assertTrue(dataFlow.states.size == 3)
//        assertTrue(dataFlow.events.last() is UIEvent.Fail)
//        assertTrue(dataFlow.events.size == 1)
//    }
//
//    @Test
//    fun `action failed flowError`() {
//        dataFlow.getAll()
//        dataFlow.add("first")
//        dataFlow.makeOnFailed()
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
//
//        assertTrue(dataFlow.states.size == 4)
//        assertTrue(dataFlow.states.last() is UIState.Failed)
//        assertTrue(dataFlow.events.size == 0)
//    }
//
//    @Test
//    fun `global action flowError`() = runBlocking {
//        dataFlow.makeGlobalError()
//        delay(100)
//
//        assertTrue(dataFlow.states[1] is UIState.Failed)
//        assertTrue(dataFlow.states.size == 2)
//        assertTrue(dataFlow.events.size == 0)
//    }
//
//    @Test
//    fun `child io action flowError`() {
//        dataFlow.getAll()
//        dataFlow.add("first")
//        dataFlow.childIOError()
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
//
//        assertTrue(dataFlow.states.last() is UIState.Failed)
//        assertTrue(dataFlow.states.size == 4)
//        assertTrue(dataFlow.events.size == 0)
//    }
//
//    @Test
//    fun `child io action`() = runBlocking {
//        dataFlow.getAll()
//        dataFlow.add("first")
//        dataFlow.childIO()
//        delay(200)
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
//        assertEquals(TodoListState(listOf(Todo("first"), Todo("LongTodo"))), dataFlow.states[3])
//
//        assertTrue(dataFlow.states.size == 4)
//        assertTrue(dataFlow.events.size == 0)
//    }
//
//    @Test
//    fun `child action flowError`() = runBlocking {
//        dataFlow.getAll()
//        dataFlow.asyncChildError()
//        delay(500)
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//        assertTrue(dataFlow.states.last() is UIState.Failed)
//
//        assertTrue(dataFlow.states.size == 3)
//        assertTrue(dataFlow.events.size == 0)
//    }
//
//    @Test
//    fun `cancel test`() = runBlocking {
//        dataFlow.getAll()
//        dataFlow.longWait()
//        delay(300)
//        dataFlow.cancel()
//
//        assertEquals(UIState.Empty, dataFlow.states[0])
//        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
//
//        assertTrue(dataFlow.states.size == 2)
//        assertTrue(dataFlow.events.size == 0)
//    }

}