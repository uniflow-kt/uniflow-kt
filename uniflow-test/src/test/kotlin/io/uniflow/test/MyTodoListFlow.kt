package io.uniflow.test

import io.uniflow.core.flow.StackDataFlow
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class MyTodoListFlow(private val repository: MyTodoRepository) : StackDataFlow() {

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

    fun add(title: String) = setState { currentState ->
        if (currentState is TodoListState) {
            val added = repository.add(title)
            if (added) {
                repository.getAllTodo().mapToTodoListState()
            } else {
                sendEvent(UIEvent.Fail("Can't add '$title'"))
            }
        } else {
            sendEvent(UIEvent.Fail("Can't add todo - List not loaded"))
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
            sendEvent(UIEvent.Fail("Can't make done - List not loaded"))
        }
    }

    fun childIOError() = withState {
        onIO {
            error("Boom on IO")
        }
    }

    fun asyncChildError() = withState {
        async {
            delay(200)
            error("child boom")
        }
        async {
            delay(1000)
            repository.add("LongTodo")
            repository.getAllTodo()
        }.await().mapToTodoListState()
    }

    fun longWait() = setState {
        delay(1000)
        repository.add("LongTodo")
        repository.getAllTodo().mapToTodoListState()
    }

    fun makeOnError() = withState {
        error("boom")
    } onError { sendEvent(UIEvent.Fail("Event logError", it)) }

    fun makeGlobalError() = withState {
        error("global boom")
    }

    override suspend fun onError(error: Throwable) {
        setState { UIState.Failed("Failed state", error) }
    }
}

data class TodoListState(val todos: List<Todo>) : UIState()

fun List<Todo>.mapToTodoListState() = TodoListState(this)



