package io.uniflow.test.data

import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState


data class TodoListState(val todos: List<Todo>) : UIState()
data class TodoListUpdate(val todo: Todo) : UIEvent()

fun List<Todo>.mapToTodoListState() = TodoListState(this)
