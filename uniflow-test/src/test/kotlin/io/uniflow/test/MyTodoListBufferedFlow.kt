package io.uniflow.test

import io.uniflow.core.flow.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class MyTodoListBufferedFlow(private val repository: MyTodoRepository) : StackActorFlow() {

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

    fun asyncChildError() = withState {
        async {
            delay(200)
            error("child boom")
        }
        async {
            delay(1000)
            repository.add("LongTodo")
        }.await()
    }

    fun longWait() = setState {
        delay(1000)
        repository.add("LongTodo")
        repository.getAllTodo().mapToTodoListState()
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



