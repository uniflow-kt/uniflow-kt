package io.uniflow.test.data

import TodoListState
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.flow.fromState
import io.uniflow.core.sample.StackFlow
import io.uniflow.core.threading.onIO
import kotlinx.coroutines.delay
import mapToTodoListState

class TodoStackActorFlow(private val repository: TodoRepository) : StackFlow() {

    init {
        setState { UIState.Empty }
    }

    fun getAll() = setState {
        repository.getAllTodo().mapToTodoListState()
    }

    fun filterDones() = setState { current ->
        when (current) {
            is TodoListState -> current.copy(current.todos.filter { it.done })
            else -> sendEvent(UIEvent.Fail("Can't filter as without todo list"))
        }
    }

    fun add(title: String) = fromState<TodoListState> {
        val added = repository.add(title)
        if (added) {
            repository.getAllTodo().mapToTodoListState()
        } else {
            sendEvent(UIEvent.Fail("Can't add '$title'"))
        }
    }

    fun done(title: String) = setState { currentState ->
        if (currentState is TodoListState) {
            val done = repository.isDone(title)
            if (done) {
                repository.getAllTodo().mapToTodoListState()
            } else {
                sendEvent(UIEvent.Fail("Can't make done '$title'"))
            }
        } else {
            sendEvent(UIEvent.BadOrWrongState(getCurrentState()))
        }
    }

    fun childIO() = setState {
        onIO {
            delay(100)
            repository.add("LongTodo")
            repository.getAllTodo().mapToTodoListState()
        }
    }

    fun childIOError() = withState {
        onIO {
            error("Boom on IO")
        }
    }

    fun longWait() = setState {
        delay(1000)
        repository.add("LongTodo")
        repository.getAllTodo().mapToTodoListState()
    }

    fun flow() = stateFlow {
        setState { UIState.Empty }
        setState { UIState.Loading }
        setState { UIState.Success }
    }

    fun flowError() = stateFlow({
        setState { UIState.Loading }
        error("boom")
        setState { UIState.Success }
    }, { error, _ -> UIState.Failed(error = error) })

    fun makeOnError() = withState(
            {
                error("boom")
            },
            { error, _ -> sendEvent(UIEvent.Fail("Event logError", error)) })

    fun makeOnStateError() = setState(
            {
                error("boom")
            },
            { error, _ -> sendEvent(UIEvent.Fail("Event logError", error)) })


    fun makeGlobalError() = withState {
        error("global boom")
    }

    override suspend fun onError(error: Exception, currentState: UIState?) {
        setState { UIState.Failed("Failed state", error, currentState) }
    }
}



