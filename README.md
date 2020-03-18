StarlingDemo
============

Author: Hristo M. Stefanov

This demo app for Android shows how to use the Starling bank public API to build
a "Round up" feature.

When you start the app, the first screen asks for an access
token for a sandbox customer ([get from here](https://developer.starlingbank.com/sandbox/select)).
On the next screen you can select an account and transfer the calculated Round Up amount
for a week to a Savings Goal. The app provides a function for creating new Savings Goals.
The Starling sandbox provides Auto-simulator for auto populating customer's history with different transactions.


## Opening the project in Android Studio

It's best to open the project by using the **File > New > Import project** command.

If the build process fails, try:
* **File > Sync project with gradle files**
* **Build > Rebuild Project**
* **File > Invalidate caches / Restart**

## Scope
The keep the effort within reasonable limits, things that some could
consider necessary are deliberately left out of scope.

**Out of scope**:
* Savings goals details like description, picture, target
* UI polishing and theming
* Progress/busy indicator
* Swipe to refresh
* Support for older devices
* Maximum test coverage
* Optimizations
* User friendly error messages
* Caching
* Offline mode
* and others :)

##  Testing
Even though maximum test coverage was out of scope, for demo purposes
the project includes 3 types of unit tests:
* 8 unit tests of CalcRoundUpInteractor
* 1 unit test of AccountsViewModel
* 5 UI unit tests of CreateSavingsGoalFragment

## Architecture
The author aims to follow the ideas in the "*Clean architecture*" book by
Robert C. Martin.

Architectural decisions for patterns and techniques:
* MVVM
* Single activity app
* Data-binding (automatic)
* Coroutines with structured concurrency
* Dependency injection (automatic)
* Shared state instead of shared view model
