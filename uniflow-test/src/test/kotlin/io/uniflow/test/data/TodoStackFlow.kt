package io.uniflow.test.data

import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState
import io.uniflow.core.flow.onIO
import io.uniflow.core.flow.stateFlowFrom
import io.uniflow.core.sample.StackFlow
import kotlinx.coroutines.delay

class TodoStackFlow(private val repository: TodoRepository) : StackFlow() {

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

//    fun asyncChildError() = withState {
//        async {
//            delay(200)
//            error("child boom")
//        }
//        async {
//            delay(1000)
//            repository.add("LongTodo")
//        }.await()
//    }

    fun longWait() = setState {
        delay(1000)
        repository.add("LongTodo")
        repository.getAllTodo().mapToTodoListState()
    }

    fun flow() = stateFlow {
        setState(UIState.Empty)
        setState(UIState.Loading)
        setState(UIState.Success)
    }

    fun flowFrom() = stateFlowFrom<UIState.Empty> {
        setState(UIState.Empty)
        setState(UIState.Loading)
        setState(UIState.Success)
    }

    fun flowFromError() = stateFlowFrom<UIState.Loading> {
        setState(UIState.Empty)
        setState(UIState.Loading)
        setState(UIState.Success)
    }

    fun makeOnError() = withState(
            {
                error("boom")
            },
            { error -> sendEvent(UIEvent.Fail("Event logError", error)) })

    fun makeOnFailed() = withState(
            {
                error("boom")
            },
            { error -> UIState.Failed(error.message, error) })


    fun makeGlobalError() = withState {
        error("global boom")
    }

    override suspend fun onError(error: Exception) {
        setState { UIState.Failed("Failed state", error) }
    }
}

data class TodoListState(val todos: List<Todo>) : UIState()

fun List<Todo>.mapToTodoListState() = TodoListState(this)



