package io.uniflow.test

import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.flow.getStateAsOrNull
import io.uniflow.core.logger.SimpleMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.logger.UniFlowLoggerTestRule
import io.uniflow.test.data.*
import io.uniflow.test.rule.TestDispatchersRule
import io.uniflow.test.validate.validate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

class ActorFlowTest {
    companion object {
        init {
            UniFlowLogger.init(SimpleMessageLogger(UniFlowLogger.FUN_TAG, debugThread = true))
        }

        @JvmStatic
        @get:ClassRule
        val uniFlowLoggerTestRule = UniFlowLoggerTestRule()
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesMainDispatcherRule = TestDispatchersRule()

    val repository = TodoRepository()
    lateinit var dataFlow: TodoStackActorFlow

    @Before
    fun before() {
        dataFlow = TodoStackActorFlow(repository)
    }

    @Test
    fun `is valid`() {
        validate<TodoStackActorFlow>()
    }


    @Test
    fun `empty state`() {
        assertEquals(UIState.Empty, dataFlow.states.first())
    }

    @Test
    fun `get all`() {
        dataFlow.getAll()
        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
    }

    @Test
    fun `get all - get state`() {
        dataFlow.getAll()
        val state = dataFlow.getStateAsOrNull<TodoListState>()
        assertEquals(TodoListState(emptyList()), state)
    }

    @Test
    fun `add one`() {
        dataFlow.getAll()
        dataFlow.add("first")

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
    }

    @Test
    fun `add one - fail`() {
        dataFlow.add("first")

        assertEquals(UIState.Empty, dataFlow.states[0])

        assertTrue(dataFlow.events[0] is UIEvent.BadOrWrongState)
    }

    @Test
    fun `done`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.done("first")

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
        assertEquals(TodoListState(listOf(Todo("first", true))), dataFlow.states[3])
    }

    @Test
    fun `filter dones`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.add("second")
        dataFlow.done("first")
        dataFlow.filterDones()

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
        assertEquals(TodoListState(listOf(Todo("first"), Todo("second"))), dataFlow.states[3])
        assertEquals(TodoListState(listOf(Todo("second"), Todo("first", true))), dataFlow.states[4])
        assertEquals(TodoListState(listOf(Todo("first", true))), dataFlow.states[5])
    }

    @Test
    fun `done - fail`() {
        dataFlow.getAll()
        dataFlow.done("first")

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])

        assertTrue(dataFlow.events[0] is UIEvent.Fail)
    }

    @Test
    fun `action error`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.makeOnError()

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])

        assertTrue(dataFlow.states.size == 3)
        assertTrue(dataFlow.events[0] is UIEvent.Fail)
        assertTrue(dataFlow.events.size == 1)
    }

    @Test
    fun `action state error`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.makeOnStateError()

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])

        assertTrue(dataFlow.states.size == 3)
        assertTrue(dataFlow.events[0] is UIEvent.Fail)
        assertTrue(dataFlow.events.size == 1)
    }

    @Test
    fun `global action error`() = runBlocking {
        dataFlow.makeGlobalError()
        delay(100)

        assertTrue(dataFlow.states[1] is UIState.Failed)
        assertTrue(dataFlow.states.size == 2)
        assertTrue(dataFlow.events.size == 0)
    }

    @Test
    fun `child io action error`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.childIOError()

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])

        assertTrue(dataFlow.states[3] is UIState.Failed)
        assertTrue(dataFlow.states.size == 4)
        assertTrue(dataFlow.events.size == 0)
    }

    @Test
    fun `child io action`() = runBlocking {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.childIO()
        delay(200)

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(TodoListState(listOf(Todo("first"))), dataFlow.states[2])
        assertEquals(TodoListState(listOf(Todo("first"), Todo("LongTodo"))), dataFlow.states[3])

        assertTrue(dataFlow.states.size == 4)
        assertTrue(dataFlow.events.size == 0)
    }

    @Test
    fun `cancel test`() = runBlocking {
        dataFlow.getAll()
        dataFlow.longWait()
        delay(300)
        dataFlow.cancel()

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])

        assertTrue(dataFlow.states.size == 2)
        assertTrue(dataFlow.events.size == 0)
    }

    @Test
    fun `test chunk update`() {
        dataFlow.getAll()
        dataFlow.notifyUpdate()

        assertTrue(dataFlow.states.size == 3)
        assertTrue(dataFlow.states.last() is TodoListState)
        assertTrue(dataFlow.events.size == 1)
        assertTrue(dataFlow.events.last() is TodoListUpdate)
    }

    @Test
    fun `cancel before test`() = runBlocking {
        dataFlow.getAll()
        dataFlow.cancel()
        dataFlow.longWait()

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])

        assertTrue(dataFlow.states.size == 2)
        assertTrue(dataFlow.events.size == 0)
    }

    @Test
    fun `flow test`() = runBlocking {
        dataFlow.testFlow()
        delay(20)

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(UIState.Loading, dataFlow.states[1])
        assertEquals(UIState.Success, dataFlow.states[2])
        assertTrue(dataFlow.states.size == 3)
        assertTrue(dataFlow.events.size == 0)
    }

    @Test
    fun `flow order test`() = runBlocking {
        assertEquals(UIState.Empty, dataFlow.states[0])
        dataFlow.states.clear()

        val max = 50
        (1..max).forEach {
            dataFlow.testFlow()
        }
        delay(max.toLong() * 20)
        assertTrue(dataFlow.states.size == max * 2)
        assertTrue(dataFlow.events.size == 0)

        dataFlow.states.filterIndexed { index, _ -> index % 2 == 0 }.all { state -> state == UIState.Loading }
        dataFlow.states.filterIndexed { index, _ -> index % 2 != 0 }.all { state -> state == UIState.Success }
        Unit
    }

    @Test
    fun `flow boom test`() = runBlocking {
        dataFlow.testBoomFlow()

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(UIState.Loading, dataFlow.states[1])
        assertTrue(dataFlow.states[2] is UIState.Failed)
        assertTrue(dataFlow.states.size == 3)
        assertTrue(dataFlow.events.size == 0)
    }

}
