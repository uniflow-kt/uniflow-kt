package io.uniflow.test.data

import io.uniflow.core.flow.UIState


data class TodoListState(val todos: List<Todo>) : UIState()

fun List<Todo>.mapToTodoListState() = TodoListState(this)
