package io.uniflow.test.multi

import io.uniflow.core.flow.data.UIData
import io.uniflow.core.flow.data.UIState
import io.uniflow.test.data.TodoRepository
import io.uniflow.test.data.mapToTodoListState
import io.uniflow.test.impl.AbstractSampleFlow
import io.uniflow.test.impl.defaultPublisher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MultiFlow(private val repository: TodoRepository) : AbstractSampleFlow(UIState.Empty) {

    val pub1 = defaultPublisher(UIState.Empty,"#1")
    val pub2 = defaultPublisher(UIState.Empty,"#2")
//    override val defaultDataPublisher = pub1

    init {
        action { pub1.setState { UIState.Empty } }
        action { pub2.setState { UIState.Empty } }
    }

    fun addMulti(todo : String) = action {
        repository.add(todo)
        val todos = repository.getAllTodo()

        println("set state -> $todo")
        pub1.setState { todos.mapToTodoListState() }
        pub2.setState { CountState(todos.size) }
    }

    fun shoudlFail(){
        action {

        }
    }

    fun assertReceived(data1: List<UIData>,data2: List<UIData>) {
        assert(pub1.data == data1) { "Wrong values\nshould have ${data1}\nbut was ${pub1.data}" }
        assert(pub2.data == data2) { "Wrong values\nshould have ${data2}\nbut was ${pub2.data}" }
    }
}

data class CountState(val c : Int) : UIState()