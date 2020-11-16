
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin, using Kotlin coroutines and open to functional programming

#### ⚒ Team - (Arnaud Giuliani, Marcin Chrapowicz, Erik Huizinga)

## Changelog

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

