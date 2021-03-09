package io.uniflow.test.data

import java.util.concurrent.ConcurrentLinkedDeque


data class Todo(val title: String, val done: Boolean = false)

class TodoRepository {

    private val allTodo = ConcurrentLinkedDeque<Todo>()

    fun getAllTodo() = allTodo.toList()

    fun isDone(title: String): Boolean {
        val todo = allTodo.firstOrNull { it.title == title }
        todo?.let {
            val done = it.copy(done = true)
            remove(title)
            allTodo.add(done)
        }
        return todo != null
    }

    fun add(title: String): Boolean {
        return allTodo.add(Todo(title))
    }

    fun remove(title: String): Boolean {
        return allTodo.removeIf { it.title == title }
    }
}
