
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin, using Kotlin coroutines and open to functional programming

#### ⚒ Team - (Arnaud Giuliani, Marcin Chrapowicz, Erik Huizinga)

## Changelog

### 1.1.0

- Fix issue with sending logs for state and event #83 @MarcinChrapowicz

### 1.0.9

- NotifyStateUpdate fix for LiveDataPublisher

### 1.0.8

- Fragment set correct owner ID #81 @MarcinChrapowicz

### 1.0.7

- Incorrect order in verifySequence #75 @MarcinChrapowicz
- Remove restriction of pushing state #78 @MarcinChrapowicz
- Fragment add extension for state and events #79 @MarcinChrapowicz

### 1.0.6

- Bug consuming events in two places #73 @nicbel
- Testing improvment - verifySequence #69 @MarcinChrapowicz

### 1.0.5

- `LiveDataPublisher` avoid to send default state automatically. Must be done in ViewModel init phase.

### 1.0.4

- `states` is exposed from AndroidDataFlow (to help consumes states for Jetpack Compose)
- `onState`, `getStateOrNull()` operators fixed to not be suspended
- `letOnState` execute lambda on given state, return a result

### 1.0.3

- `Gradle` force Java 8 Compile for Android projects
- `OnFlow` operator to help emit Actions from Coroutines Flow<T>

### 1.0.2

- `Maven Central` migration, publishing the project to change group id from `io.uniflow` to `org.uniflow-kt`
- `Reworked` DataFlow type hierarchy to avoid leak internals
- `Reworked` Dispatcher engine to more efficient running actions
- `Reworked` DataFlow `onError` has now only 2 params (exception and state). We can use directly `setState`/`sendEvent` from there
- `DataPublisher` design reworked to allow multiple streams
- `action` & `actionOn` won't leak any internal `Action` reference anymore
- `Merged` Android/AndroidX implementation of Uniflow. `uniflow-android` bring the AndroidX API, as the old support is deprecated
- `Reworked` TestDispatcher with 2 implementations: `UniflowTestDispatchersRule` (flatten on the same dispatcher, make async code more sequential) and `DefaultTestDispatchersRule` (allow dispatch on different Dispatchers)
- Documentation update

## 0.12.x

### 0.12.0

- `added` new Event/EventConsumer API to avoid need of take/peek operator, and help simplify the use of UIEvent.  

## 0.11.x

### 0.11.7

- `added` an empty logger (`EmptyLogger`, logs nothing): use `UniFlowLogger.init(EmptyLogger())` or `UniFlowLogger.empty()`
- `fixed` Fatal exception in UIState - https://github.com/uniflow-kt/uniflow-kt/pull/48
- `fixed` Error stacktrace on std out - https://github.com/uniflow-kt/uniflow-kt/pull/47

### 0.11.6

- `fixed` fix UIError message to include `origin` exception

### 0.11.5

- `fixed` compact message logs

### 0.11.4

- `fixed` test message comparison

### 0.11.3

- `fixed` android simple tester `verifySequence` API to help verify which state data is failing in the list
- `fixed` UIError mapping
- `fixed` Set initial state synchronously to avoid race condition - https://github.com/uniflow-kt/uniflow-kt/pull/41
- `added` More logs
- `added` `onTakeEvents` & `onPeekEvents` for Android observer components. Do directly take() or peek() on the event data & give it if non null

### 0.11.2

- `fixed` fix extensions `UIError` 

### 0.11.1

- `added` Add `UIError` object to help wrap exception make it comparable. (#35)
- `fixed` Parameterize `TestCoroutineDispatcher` in `TestDispatchers` and `TestDispatchersRule` (#30)
- `fixed` Make `Event<out T>` covariant with `UIEvent` (#32)

### 0.11.0

- `update` Kotlin 1.3.72 & Kotlin Coroutines 1.3.5
- `update` Arrow integration with Either type in `uniflow-arrow` module. Arrow update to 0.10.5 (👍 Borja Quevedo)
- `update` internal components design: Keep DataFlow as interface and use components: ActionDispatcher, ActionReducer, UiDataPublisher & UiDataStore. Help to avoid expose internal functions API
- `added` better testing tools and introduce `createTestObserver` to help create `TestViewObserver`. It allow to use `assertReceived` data states & events
- `fixed` Endless loop fix - (👍 Grzegorz Gajewski)
- `added`  Add UniFlowLogger.default() to reset to the default Logger 
- `fixed`  Use TestCoroutineDispatcher instead of Dispatchers.Unconfined

### 0.10.3

- `added` better test tools to compare list of states/events directly

### 0.10.2

- `fixed` DataFlow is now adding new action with `send` on Actor, anwith a coroutine call on IO

### 0.10.1

- `update` AndroidDataFlow() use `UIState.Empty` as default state 

### 0.10.0

_no backward compatibility API with previous version!_

- `update` Rework all internals app design components, an action is now a Kotlin Flow emitting UISTate/UIEvent
- `update` Non nullable default state
- `add` Unify actions API to return `ActionFlow` and use `setState` and not having 2 kinds of action flow
- `update` `setState { }` / `fromState<> { }` API are now `action { }` and `actionOn<> { }`. You need to use `setState` to specify a state.
- `update` `stateFlow { }` / `stateFlowFrom<> { }` API are now `action { }` and `actionOn<> { }`

### 0.8.x

#### 0.8.5

- merge `AndroidDataFlow` and `AndroidActorFlow` classes in both Android libs to provide an actor based ViewModel by default, to ensure event scheduling ordered.

