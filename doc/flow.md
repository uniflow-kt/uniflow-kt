
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Collecting a coroutines Flow<T> to run Actions

When we have to use coroutines [Flow<T>`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) 
we need to collect the resulting value to finally emit our state ou event data.

Below, on a `flow` that emits value from 1 to 3, we will collect it to open a new action and set a new state: 
```kotlin
fun actionList() {
    // Launch a job
    viewModelScope.launch {
        // get our Flow
        val flow = flow { ... }
        // Collect it
        flow.collect { value ->
            // Make an action per value
            action {
                setState { CountState(value) }
            }
        }
    }
}
```

## OnFlow - Wrapping directly Coroutines Flow<T> to Actions

We can work with Flow<T> data in a safer way. Uniflow offers the `onFlow()` function like follow:

```kotlin
fun actionList() {
    // Launch a job
    viewModelScope.launch {
        // get our Flow
        val flow = flow { ... }
        
        // Collect from Flow and emit actions
        onFlow( { flow } ){ value ->
            // Push new state
            setState { CountState(value) }
        }
    }
}
```

By default, any error encountered in `onFlow`, from the observed `flow` will emit an action error. We can also provide an error function if needed, like on a regular action:

```kotlin
fun actionList() {
    // Launch a job
    viewModelScope.launch {
        // get our Flow
        val flow = flow { ... }
        
        // Collect from Flow and emit actions
        onFlow( 
            flow = { flow },
            doAction = { value ->
                // Push new state
                setState { CountState(value) }
            },
            onError = { error, state -> 
                // Handle an error
            }
        )
    }
}
```

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)


