StarlingDemo
============
Author: Hristo M. Stefanov

This Android app is a demo and is not intended for actual use. It demos
using a banking REST API to implement a "Round up" feature with
Kotlin coroutines and Jetpack Architecture Components.

It can be run against the Starling Bank's API sandbox environment using
the `sandbox` build variant or against an embedded **mock service** by
using the `debug` build variant (the default).


When you start the app, the first screen asks for an access token.
For the **mock service** (`debug` build variant) you can provide any string.  
For a sandbox customer get it from ([here](https://developer.starlingbank.com/sandbox/select)).

On the next screen you can select an account and transfer the calculated Round Up amount
for a week to a Savings Goal. The app provides a function for creating new Savings Goals.

NOTE: The Starling API sandbox provides Auto-simulator for auto populating customer's  
history with different transactions.


## Opening the project in Android Studio

It's best to open the project by using the **File > New > Import project** command.

If the build process fails, try:
* **File > Sync project with gradle files**
* **Build > Rebuild Project**
* **File > Invalidate caches / Restart**

## Out of scope
* Savings goals details like description, picture, target
* Progress/busy indicator
* Swipe to refresh
* Support for older devices
* Maximum test coverage
* Optimizations
* User friendly error messages
* Caching
* Offline mode

##  Testing
Even though maximum test coverage is out of scope, for demo purposes
the project includes 3 types of unit tests:
* unit tests for business logic (CalcRoundUpInteractorTest)
* unit tests for view models (AccountsViewModelTest and CreateSavingsGoalViewModelTest)
* UI unit tests (CreateSavingsGoalFragmentTest)

## Architecture
The author aims to follow the ideas in the "*Clean architecture*" book by
Robert C. Martin.

Architectural decisions for patterns and techniques:
* MVVM
* Single activity app
* Data-binding (automatic)
* Coroutines with structured concurrency
* Dependency injection (Dagger 2)
* EventBus for local broadcasting