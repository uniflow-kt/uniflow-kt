
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Default Stream

By default a `DataFlow` component provides a default `DataPublisher`. For Android we have a default `LiveDataPublisher`. This is why we can directly use `setState` or `sendEvent` in an action.

```kotlin
class MultiFlow : AndroidDataFlow() {
    
    fun countString(string: String) = action {
        setState { ... }
    }
}
```

## Multiple Streams

We can also defin multiple streams. We just need to declare it as a property with `liveDataPublisher`. In an action bloc, we can use both streams to send data:

```kotlin
class MultiFlow : AndroidDataFlow() {

    // define 2 new streams
    val messageDataFlow = `liveDataPublisher`(UIState.Empty, "publisher_message")
    val counterDataFlow = liveDataPublisher(UIState.Empty, "publisher_count")
    
    // Run on action to use the streams
    fun countString(string: String) = action {
        // send new data state
        messageDataFlow.setState { MessageState(string) }
        counterDataFlow.setState { CountState(string.length) }
    }
}
```

_Warning_: `actionOn<T>` function doesn only work with default publisher. To make a guard for given state, you can use `onState<T>(){ ... }` in an action block

For testing, we can create TestObserver for each stream:

```kotlin
@Before
fun before() {
    dataFlow = MultiFlow()
    
    // Create observer per stream
    messageObserver = dataFlow.messageDataFlow.createTestObserver()
    countObserver = dataFlow.counterDataFlow.createTestObserver()
}
```

Finally, we just need to verify the content of each stream:

```kotlin
@Test
fun `actions are done in orders`() = runBlocking {
    val message = "test"
    dataFlow.countString(message)

    // verify content on each stream
    messageObserver.verifySequence(
        MessageState(message)
    )
    countObserver.verifySequence(
        CountState(message.length)
    )
}
```

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)



