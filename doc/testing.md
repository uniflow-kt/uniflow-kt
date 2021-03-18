
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Testing 🚑

### Observing States/Events

Create your ViewModel instance and create test observer with `createTestObserver():

```kotlin
lateinit var dataFlow: OurDataFlow
lateinit var tester: TestViewObserver

@Before
fun before() {
    dataFlow = OurDataFlow()
    // create test observer
    tester = dataFlow.createTestObserver()
}
```

Now you can test incoming states & events with `verifySequence`:

```kotlin
@Test
fun `run some states`() {
    dataFlow.action1()
    dataFlow.action2()
    dataFlow.action3()
    
    // tester will receive states in following orders
    tester.verifySequence(
        UIState.Empty,
        MyState1,
        MyState2,
        MyState3
    )
}
```

### Mocking Observer 

Create your ViewModel instance and mock your states observer with `mockObservers()`:

```kotlin
lateinit var dataFlow: OurDataFlow
lateinit var tester: MockViewObserver

@Before
fun before() {
    dataFlow = OurDataFlow()
    // create mocked observer 
    view = dataFlow.mockObservers()
}
```

Now you can test incoming states & events with `hasState`:

```kotlin
@Test
fun `run some states`() {
    dataFlow.action1()

    // tester will receive following state
    tester.assertHasState(MyState1)
}
```

## JUnit Rules

### Logging

- Change Logging during tests:

```kotlin
companion object {
    init {
        UniFlowLogger.init(SimpleMessageLogger(UniFlowLogger.FUN_TAG, debugThread = true))
    }

    @JvmStatic
    @get:ClassRule
    val uniFlowLoggerTestRule = UniFlowLoggerTestRule()
}
```

### Test Dispatchers

- `TestDispatcherRule` - simply uses Coroutines TestDispatcher to run your test (really dispatch on IO or other context)

```kotlin
@get:Rule
val testDispatcherRule = TestDispatcherRule()

private val testCoroutineDispatcher = testDispatchersRule.testCoroutineDispatcher
```

- `UniflowTestDispatchersRule` - uses Uniflow Test Dispatcher (flatten on same main context) to run your test, in sequence

```kotlin
@get:Rule
val testDispatcherRule = UniflowTestDispatchersRule()
```


----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)
