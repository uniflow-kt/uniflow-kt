package io.uniflow.test

import io.uniflow.core.flow.data.UIError
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.DebugMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.logger.UniFlowLoggerTestRule
import io.uniflow.test.data.Todo
import io.uniflow.test.data.TodoListState
import io.uniflow.test.data.TodoRepository
import io.uniflow.test.rule.TestDispatchersRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StackFlowTest {
    companion object {
        init {
            UniFlowLogger.init(DebugMessageLogger())
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
    fun `empty state`() {
        dataFlow.assertReceived(UIState.Empty)
    }

    @Test
    fun `get all`() {
        dataFlow.getAll()
        dataFlow.assertReceived(UIState.Empty, TodoListState(emptyList()))
    }

    @Test
    fun `add one`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))))
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

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                TodoListState(listOf(Todo("first", true))))
    }

    @Test
    fun `filter dones`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.add("second")
        dataFlow.done("first")
        dataFlow.filterDones()

        dataFlow.assertReceived(
                UIState.Empty,
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
                UIEvent.Error(message = "Can't make done 'first'"))
    }

    @Test
    fun `action error`() {
        dataFlow.getAll()
        dataFlow.add("first")
        dataFlow.makeOnError()

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList()),
                TodoListState(listOf(Todo("first"))),
                UIEvent.Error("Event logError", UIError("boom"))
        )
        // assert(dataFlow.last is UIEvent.Error)
    }

    @Test
    fun `global action error`() = testCoroutineDispatcher.runBlockingTest {
        val error = IllegalStateException("global boom")
        dataFlow.makeGlobalError()
        delay(100)

        dataFlow.assertReceived(
                UIState.Empty,
                UIState.Failed("Got error $error", error))
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
    fun `cancel before test`() = testCoroutineDispatcher.runBlockingTest {
        dataFlow.getAll()
        dataFlow.close()
        dataFlow.longWait()

        dataFlow.assertReceived(
                UIState.Empty,
                TodoListState(emptyList())
        )
    }

}