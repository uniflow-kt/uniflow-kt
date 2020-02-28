
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin, using Kotlin coroutines and open to functional programming

## Changelog

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


