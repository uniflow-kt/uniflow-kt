package io.uniflow.test

import java.util.concurrent.ConcurrentLinkedDeque


data class Todo(val title: String, val done: Boolean = false)

class MyTodoRepository {

    private val allTodo = ConcurrentLinkedDeque<Todo>()
    private val max = 10

    fun getAllTodo() = allTodo.toList()

    fun addTodo(title: String): Boolean {
        val canAdd = allTodo.size < max
        if (canAdd) {
            allTodo.add(Todo(title))
        }
        return canAdd
    }
}
