
# Uniflow 🦄- Simple Unidirectionnel Data Flow for Android & Kotlin

## Coroutines inside ✨

Every action launched by a DataFlow is runned in a coroutines context, by default on IO Thread. Then you know that by default, we launch things in background for you 👍

If you need to switch context of the current thread you use from your action:

- `onIO { }` - equivalent of withContext(IO dispatcher)
- `onMain { }` - equivalent of withContext(IO Main)
- `onDefault { }` - equivalent of withContext(IO default)

And if you need to launch a job on different thread, use:

- `launchOnIO { }` - equivalent of withContext(IO dispatcher)
- `launchOnMain { }` - equivalent of withContext(IO Main)
- `launchOnDefault { }` - equivalent of withContext(IO default)

_note_: we simplify here the wirting of such threading operator, as we also make an asbtaction around the used dispatcher to help further testing. See testing section below.


----

## [Back To Documentation Topics](../README.md#getting-started--documentation-)


