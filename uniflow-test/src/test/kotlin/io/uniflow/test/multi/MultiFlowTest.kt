package io.uniflow.test.multi

import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.SimpleMessageLogger
import io.uniflow.core.logger.UniFlowLogger
import io.uniflow.core.logger.UniFlowLoggerTestRule
import io.uniflow.test.data.TodoRepository
import io.uniflow.test.data.mapToTodoListState
import io.uniflow.test.rule.TestDispatchersRule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

class MultiFlowTest {
    companion object {
        init {
            UniFlowLogger.init(SimpleMessageLogger(UniFlowLogger.FUN_TAG, showDebug = false))
        }

        @JvmStatic
        @get:ClassRule
        val uniFlowLoggerTestRule = UniFlowLoggerTestRule()
    }

    @get:Rule
    val testDispatchersRule = TestDispatchersRule()

    private val testCoroutineDispatcher = testDispatchersRule.testCoroutineDispatcher

    val repository = TodoRepository()
    lateinit var dataFlow: MultiFlow

    @Before
    fun before() {
        dataFlow = MultiFlow(repository)
    }

    @Test
    fun `empty state`() {
        dataFlow.assertReceived(
                listOf(
                        UIState.Empty
                ),
                listOf(
                        UIState.Empty
                )
        )
    }

    @Test
    fun `simple multi action`() {
        dataFlow.addMulti("toto")
        val allTodo = repository.getAllTodo()
        dataFlow.assertReceived(
                listOf(
                        UIState.Empty,
                        allTodo.mapToTodoListState()
                ),
                listOf(
                        UIState.Empty,
                        CountState(allTodo.size)
                )
        )
    }

    @Test
    fun `manual guard`() {
        dataFlow.manualGuard("toto")
        dataFlow.assertReceived(
                listOf(
                        UIState.Empty
                ),
                listOf(
                        UIState.Empty
                )
        )
    }

    @Test
    fun `several multi action`() = runBlocking {
        val wait = 50L
        val max = 100

        GlobalScope.launch {
            (1..max / 2).forEach { i ->
                GlobalScope.launch {
                    println("-> $i")
                    dataFlow.addMulti("todo_$i")
                    dataFlow.checkMe()
                    delay(wait)
                }
            }
        }
        GlobalScope.launch {
            (max / 2..max).forEach { i ->
                GlobalScope.launch {
                    println("-> $i")
                    dataFlow.addMulti("todo_$i")
                    dataFlow.checkMe()
                    delay(wait)
                }
            }
        }

        while ((dataFlow.pub2.getState() as? CountState)?.c != max + 1) {
            delay(wait)
            dataFlow.checkMe()
        }
    }
}