StarlingDemo
============

Author: Hristo M. Stefanov

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
