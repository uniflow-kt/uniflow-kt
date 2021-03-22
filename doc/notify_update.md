
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Notify State Changes

In the cases of having a real complex State, it can be more convenient to avoid republish a state. 

For example, in the case below we would like to avoid republish the entire list of "Todo" on an update.

```kotlin
data class TodoListState(val todos: List<Todo>) : UIState()
data class TodoListUpdate(val todo: Todo) : UIEvent()
```

If we use an action, this will republish the current state entirely:

```kotlin
fun newTodo(todoTitle : String) = actionOn<TodoListState> { state ->
    // New Todo
    val t = Todo(todoTitle)
    // Let's update the list
    val list = state.todos + t
    
    // Will republish the entire list of Todo
    setState { TodoListState(list) }
}
```

Uniflow offers the `notifyStateUpdate(<state>, <event>)` function to allow to update your current state, but notifies the change with an Event. 
It's a combination of `setState` and `sendEvent`, saving the state but just pushing the event to the UI.


```kotlin
fun newTodo(todoTitle : String) = actionOn<TodoListState> { state ->
    // New Todo
    val t = Todo(todoTitle)
    // Let's update the list
    val list = state.todos + t
    
    // Notify for state change with the TodoListUpdate event
    // The new state will be TodoListState(list)
    notifyStateUpdate(TodoListState(list), TodoListUpdate(t))
}
```

On UI side, only the `TodoListUpdate` event will be received:

```kotlin
class MyActivity : AppCompatActivity(){

    fun onCreate(...) {
        
        onEvents(viewModel) { event ->
            when (val data = event.take()) {
                // only TodoListUpdate will be triggered from "newTodo" action function
                is TodoListUpdate -> onTodoUpdated(data)
            }
        }
    }
}
—
```

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)



