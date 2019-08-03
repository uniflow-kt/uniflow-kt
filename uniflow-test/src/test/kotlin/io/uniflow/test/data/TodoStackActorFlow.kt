package io.uniflow.test.data

import io.uniflow.core.flow.*
import io.uniflow.core.sample.StackActorFlow
import kotlinx.coroutines.delay

class TodoStackActorFlow(private val repository: TodoRepository) : StackActorFlow() {

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

    fun add(title: String) = setStateFrom<TodoListState> {
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

    fun asyncChildError() = withState({
        setState({
            delay(200)
            error("child boom")
        }, { error -> UIState.Failed(error = error) })

        setState {
            delay(1000)
            repository.add("LongTodo")
            repository.getAllTodo().mapToTodoListState()
        }
    }, { error -> UIState.Failed(error = error) })

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

    fun makeOnError() = withState(
            {
                error("boom")
            },
            { error -> sendEvent(UIEvent.Fail("Event logError", error)) })


    fun makeGlobalError() = withState {
        error("global boom")
    }

    override suspend fun onError(error: Throwable) {
        setState { UIState.Failed("Failed state", error) }
    }
}



