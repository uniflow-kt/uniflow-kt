
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Mapping Coroutines Flow to States

When we have to use coroutines [Flow<T>`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) 
we need to collect the resulting value to finally emit our state ou event data.

Below, on a `flow` that emits value from 1 to 3, we will collect it to open a new action and set a new state: 
```kotlin
fun actionList() {
    // Launch a job
    viewModelScope.launch {
        // get our Flow
        val flow = flow { (1..3).forEach { emit(it) } }
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

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)


