# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin, using Kotlin coroutines and open to functional programming

## Functional Coroutines, to safely make states & events 🌈

One way to handle properly dangerous calls & exceptions, is to do it with a functional approach.

### Safely wrapping results with `SafeResult` type

It will help you write your state flow in a functional way.

You can wrap any `SafeResult.Success` value like that:

```kotlin
val myData : Any ...

// wrap it as Success
success(myData) or myData.success()
```

Concerning errors, you can wrap a `SafeResult.Failure` error like follow:

```kotlin
val myError : Exception ...

// wrap it as Failure
SafeResult.raiseError(myError) or myError.failure()
```

### Wrapping unsafe expression

To help you deal with expression that can raise exceptions, we provide a result wrapper that will catch any error for you. Use the `safeValue` uniflow function to wrap an expression as `Try`:

```kotlin
// Will transform result as SafeResult.Success and any error to SafeResult.Error

fun myDangerousCall() : MyData

// will produce SafeResult<MyData>
val safeResult : SafeResult<MyData> = safeValue { myDangerousCall() }
```

Here we have the following default builders:

- `safeCall { } ` - wrap Try result (data or exception)
- `networkCall { } ` - wrap Try result, catch exception and wap it in a `NetworkException` object
- `databaseCall { } `- wrap Try result, catch exception and wap it in a `DatabaseException` object

You can also make your own safe result builder, depending on your APIs 👍

### Functional operators

You can then build expression to combine unsafe IO calls. Here we use `networkCall` to wrap Retrofit expression:

```kotlin
networkCall { weatherDatasource.geocode(targetLocation).await() }
	    .map { it.mapToLocation() ?: error("Can't map to location: $it") }
	    .flatMap { (lat, lng) -> networkCall { weatherDatasource.weather(lat, lng).await() } }
	    .map { it.mapToWeatherEntities(targetLocation) }
	    .onSuccess { weatherCache.addAll(it) }
```

Amoung the classical `Try` functional operators, we add some more:  
- `get` - get the existing value or throw current error's exception
- `getOrNull` - get the existing value or null
- `onSuccess` - do something on existing value
- `onFailure` - do something on existing error

`get` & `getOrNull` are terminal operators, they give you the final result of your functional flow.

### Building states safely

In your DataFlow ViewModel class, you will make sequence of operation to result in UIState(s):

```kotlin
fun loadNewLocation(location: String) = actionOn<WeatherListState> {
        getWeatherForLocation(location)
                .toState( { it.mapToWeatherListState() }, { error -> UIState.Failed(error = error) })
    }
```

- `mapState` - map current value to a UIState value
- `toState` - get & map current value to a UIState (can specify Success & Failure state mapping)
- `toStateOrNull` - get & map current Success value to UIState else return null

`toState` & `toStateOrNull` are terminal operators

To send any event, use the `onSuccess` or `onFailure` operator, to send a it:

```kotlin
fun loadNewLocation(location: String) = actionOn<WeatherListState> {
        getWeatherForLocation(location)
                .onFailure { error -> sendEvent(WeatherListUIEvent.ProceedLocationFailed(location, error)) }
                .toStateOrNull { it.mapToWeatherListState() }
    }
```

### Scheduling & Testing

First don't forget to use thr following rule:

```kotlin
@get:Rule
val rule = TestDispatchersRule()
```

`TestDispatchersRule` allows you to test with a `TestCoroutineDispatcher` by default.
It flattens all scheduling to help sequential processing of all states and events.
The rule also automatically replaces the `Main` dispatcher with the test dispatcher and it detects any leaking coroutines.
You can obtain the `TestCoroutineDispatcher` from the rule and use it in your tests as follows:

```kotlin
@Test
fun `your test`() = rule.testCoroutineDispatcher.runBlockingTest { /*..*/ }
```

You can also use the `TestThreadRule`, to emulate a main thread: replace main dispatcher by a single thread context dispatcher

----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)
