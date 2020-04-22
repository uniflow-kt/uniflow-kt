package io.uniflow.test

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.SimpleMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.logger.UniFlowLoggerTestRule
import io.uniflow.test.data.Todo
import io.uniflow.test.data.TodoListState
import io.uniflow.test.data.TodoListUpdate
import io.uniflow.test.data.TodoRepository
import io.uniflow.test.impl.BadDF
import io.uniflow.test.impl.SampleFlow
import io.uniflow.test.rule.TestDispatchersRule
import io.uniflow.test.validate.validate
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
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

    @get:Rule
    val testDispatchersRule = TestDispatchersRule()

    private val testCoroutineDispatcher = testDispatchersRule.testCoroutineDispatcher

    val repository = TodoRepository()
    lateinit var dataFlow: SampleFlow

    @Before
    fun before() {
        dataFlow = SampleFlow(repository)
    }

    @Test
    fun `is valid`() {
        validate<SampleFlow>()
    }

    @Test
    fun `is not valid`() {
        try {
            validate<BadDF>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Test
    fun `empty state`() {
        assertEquals(UIState.Empty, dataFlow.states.first())
    }

    @Test
    fun `get all`() {
        dataFlow.getAll()
        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()))
    }

    @Test
    fun `add one`() {
        dataFlow.getAll()
        dataFlow.add("first")

        dataFlow.assertReceived(UIState.Empty, TodoListState(emptyList()), TodoListState(listOf(Todo("first"))))
    }

    @Test
    fun `add one - fail`() {
        dataFlow.add("first")
        dataFlow.assertReceived(UIState.Empty, UIEvent.BadOrWrongState(UIState.Empty))
    }

    @Test
    fun `done`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.done("first")

        dataFlow.assertReceived(UIState.Empty, TodoListState(emptyList()), TodoListState(listOf(Todo("first"))), TodoListState(listOf(Todo("first", true))))
    }

    @Test
    fun `filter dones`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.add("second")
        dataFlow.done("first")
        dataFlow.filterDones()

        dataFlow.assertReceived(UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                TodoListState(listOf(Todo("first"), Todo("second"))),
                TodoListState(listOf(Todo("second"), Todo("first", true))),
                TodoListState(listOf(Todo("first", true))))
    }

    @Test
    fun `done - fail`() {
        dataFlow.getAll()
        dataFlow.done("first")

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()),
                UIEvent.Error("Can't make done 'first'"))
    }

    @Test
    fun `action error`() {
        val error = IllegalStateException("boom")
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.makeOnError()

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                UIEvent.Error("Event logError", error)
        )
    }

    @Test
    fun `global action error`() = testCoroutineDispatcher.runBlockingTest {
        val error = IllegalStateException("global boom")
        dataFlow.makeGlobalError()
        delay(100)

        dataFlow.assertReceived(
                UIState.Empty,
                UIState.Failed("Got error $error", error)
        )
    }

    @Test
    fun `child io action error`() {
        val error = IllegalStateException("Boom on IO")
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.childIOError()

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                UIState.Failed("Got error $error", error)
        )
    }

    @Test
    fun `child io action`() = testCoroutineDispatcher.runBlockingTest {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.childIO()
        delay(200)

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                TodoListState(listOf(Todo("first"), Todo("LongTodo")))
        )
    }

    @Test
    fun `cancel test`() = testCoroutineDispatcher.runBlockingTest {
        dataFlow.getAll()
        dataFlow.longWait()
        delay(300)
        dataFlow.close()

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList())
        )
    }

    @Test
    fun `test chunk update`() {
        dataFlow.getAll()
        dataFlow.notifyUpdate()

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListUpdate(Todo("t2"))
        )
    }

    @Test
    fun `cancel before test`() = testCoroutineDispatcher.runBlockingTest {
        dataFlow.getAll()
        dataFlow.close()
        dataFlow.longWait()

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList())
        )
    }

    @Test
    fun `flow test`() = testCoroutineDispatcher.runBlockingTest {
        dataFlow.testFlow()
        delay(20)

        dataFlow.assertReceived(
                UIState.Empty,
                UIState.Loading,
                UIState.Success
        )
    }

    @Test
    fun `test flow from state`() = testCoroutineDispatcher.runBlockingTest {
        dataFlow.getAll()
        dataFlow.notifyFlowFromState()
        delay(20)

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(TodoListState(emptyList()), dataFlow.states[1])
        assertEquals(UIState.Loading, dataFlow.states[2])
        assertEquals(UIState.Success, dataFlow.states[3])

        assertTrue(dataFlow.states.size == 4)
        assertTrue(dataFlow.events.size == 0)
    }

    @Test
    fun `test flow from state exception`() = testCoroutineDispatcher.runBlockingTest {
        dataFlow.notifyFlowFromState()
        delay(20)

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(UIEvent.BadOrWrongState(UIState.Empty), dataFlow.events[0])

        assertTrue(dataFlow.states.size == 1)
        assertTrue(dataFlow.events.size == 1)
    }

    @Test
    fun `flow order test`() = testCoroutineDispatcher.runBlockingTest {
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
    fun `flow boom test`() = testCoroutineDispatcher.runBlockingTest {
        dataFlow.testBoomFlow()

        assertEquals(UIState.Empty, dataFlow.states[0])
        assertEquals(UIState.Loading, dataFlow.states[1])
        assertTrue(dataFlow.states[2] is UIState.Failed)
        assertTrue(dataFlow.states.size == 3)
        assertTrue(dataFlow.events.size == 0)
    }

}