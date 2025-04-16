[![Android CI](https://github.com/hristo-stefanov/MiniBankingDemo/actions/workflows/android.yml/badge.svg)](https://github.com/hristo-stefanov/MiniBankingDemo/actions/workflows/android.yml)

MiniBankingDemo
===============
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

This project requires JDK 11 to build.

##  Test suite
The project includes various types of tests:
* BDD-style acceptance tests (in .feature files)
* Unit tests for business logic (CalcRoundUpInteractorTest)
* Unit tests for view models (AccountsViewModelTest and CreateSavingsGoalViewModelTest)
* UI unit tests (CreateSavingsGoalFragmentTest)

## Architecture
The author aims to follow the ideas in the "*Clean architecture*" book by
Robert C. Martin.

Architectural decisions for patterns and techniques:
* MVVM with data-binding
* Single activity app
* Coroutines and Flow
* Dependency injection (Dagger 2)
* EventBus for local broadcasting

## Sentry

The Sentry Android Gradle plugin will automatically integrated with OkHttp, androidx.fragment and 
other, see Sentry for Android > Gradle. Only integration with the Navigation component is "manual".

Is this option still available?
If _Performance_ section reveals no transactions, click "Enable real time updates" at the top-right
corner of the screen. If this wont help, wait for an hour :)

### Useful links

https://docs.sentry.io/product/performance/transaction-summary/#what-is-a-transaction
https://docs.sentry.io/product/sentry-basics/tracing/distributed-tracing/#traces-transactions-and-spans
https://docs.sentry.io/platforms/android/configuration/integrations/fragment/
https://docs.sentry.io/platforms/android/performance/instrumentation/automatic-instrumentation/
https://docs.sentry.io/platforms/android/performance/instrumentation/custom-instrumentation/
https://docs.sentry.io/platforms/android/configuration/options/#idle-timeout