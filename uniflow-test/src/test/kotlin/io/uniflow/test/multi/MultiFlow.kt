package io.uniflow.test.multi

import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.flow.getStateOrNull
import io.uniflow.test.data.TodoListState
import io.uniflow.test.data.TodoRepository
import io.uniflow.test.data.mapToTodoListState
import io.uniflow.test.impl.AbstractSampleFlow
import io.uniflow.test.impl.simpleListPublisher

class MultiFlow(private val repository: TodoRepository) : AbstractSampleFlow(UIState.Empty) {

    val pub1 = simpleListPublisher(UIState.Empty, "#1")
    val pub2 = simpleListPublisher(UIState.Empty, "#2")

    init {
        action {
            pub1.setState { UIState.Empty }
            pub2.setState { UIState.Empty }
        }
    }

    fun addMulti(todo: String) = action {
        repository.add(todo)
        val todos = repository.getAllTodo()

        println("new list -> ${todos.size}")
        pub1.setState { todos.mapToTodoListState() }
        pub2.setState { CountState(todos.size) }
    }

    fun manualGuard(todo: String) = action {
        pub1.getStateOrNull<TodoListState>()?.let {
            repository.add(todo)
            val todos = repository.getAllTodo()
            println("new list -> ${todos.size}")
            pub1.setState { todos.mapToTodoListState() }
            pub2.setState { CountState(todos.size) }
        } ?: println("wrong state")
    }

    fun checkMe() = action {
        val todos = (pub1.getState() as? TodoListState)?.todos?.size ?: 0
        val count = (pub2.getState() as? CountState)?.c ?: 0
        assert(count == todos) { "count:$count but was $todos" }
        println("check $todos")
    }

    fun assertReceived(data1: List<UIData>, data2: List<UIData>) {
        assert(pub1.data == data1) { "Wrong values\nshould have ${data1}\nbut was ${pub1.data}" }
        assert(pub2.data == data2) { "Wrong values\nshould have ${data2}\nbut was ${pub2.data}" }
    }

    override suspend fun onError(error: Exception, currentState: UIState) {
        action {
            pub1.setState { UIState.Failed(error = error) }
        }
    }
}

data class CountState(val c: Int) : UIState()