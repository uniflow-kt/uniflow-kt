package io.uniflow.test

import io.uniflow.core.flow.actionOn
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onIO
import io.uniflow.test.data.*
import io.uniflow.test.impl.AbstractSampleFlow
import kotlinx.coroutines.delay

class SampleFlow(private val repository: TodoRepository) : AbstractSampleFlow(UIState.Empty) {
    fun getAll() = action {
        setState {
            repository.getAllTodo().mapToTodoListState()
        }
    }

    fun filterDones() = action { current ->
        when (current) {
            is TodoListState -> setState { current.copy(current.todos.filter { it.done }) }
            else -> sendEvent(UIEvent.Error("Can't filter as without todo list"))
        }
    }

    fun add(title: String) = actionOn<TodoListState> {
        val added = repository.add(title)
        if (added) {
            setState { repository.getAllTodo().mapToTodoListState() }
        } else {
            sendEvent(UIEvent.Error("Can't add '$title'"))
        }
    }

    fun done(title: String) = action { currentState ->
        if (currentState is TodoListState) {
            val done = repository.isDone(title)
            if (done) {
                setState { repository.getAllTodo().mapToTodoListState() }
            } else {
                sendEvent(UIEvent.Error("Can't make done '$title'"))
            }
        } else {
            sendEvent(UIEvent.BadOrWrongState(this@SampleFlow.getState()))
        }
    }

    fun childIO() = action {
        onIO {
            delay(10)
            repository.add("LongTodo")
            setState { repository.getAllTodo().mapToTodoListState() }
        }
    }

    fun childIOError() = action {
        onIO {
            error("Boom on IO")
        }
    }

    fun longWait() = action {
        delay(1000)
        repository.add("LongTodo")
        setState { repository.getAllTodo().mapToTodoListState() }
    }

    fun makeOnError() = action(
            {
                error("boom")
            },
            { error, _ ->
                error.printStackTrace()
                sendEvent(UIEvent.Error("Event logError", error)) }
    )


    fun makeGlobalError() = action {
        error("global boom")
    }

    fun makeGlobalErrorOnState() = actionOn<TodoListState> {
        error("global TodoListState boom")
    }

    fun notifyUpdate() = actionOn<TodoListState> { state ->
        val t = Todo("t2")
        val list = state.todos + t

        notifyStateUpdate(TodoListState(list), TodoListUpdate(t))
    }

    fun testFlow() = action {
        setState { UIState.Loading }
        delay(10)
        setState { UIState.Success }
    }

    fun setUIStateFailed() = action {
        setState(UIState.Failed("Some message here"))
    }

    fun notifyFlowFromState() = actionOn<TodoListState> {
        setState { UIState.Loading }
        delay(10)
        setState { UIState.Success }
    }

    fun testBoomFlow() = action({
        setState { UIState.Loading }
        error("boom")
    }, { e, _ -> setState { UIState.Failed("flow boom", e) } })
}



