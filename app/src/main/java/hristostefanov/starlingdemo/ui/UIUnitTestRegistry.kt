package hristostefanov.starlingdemo.ui

import androidx.lifecycle.ViewModelProvider

object UIUnitTestRegistry {
    // Replacing the ViewModelProvider.Factory instead of ViewModel itself in Fragments
    // being UI unit tested is the working approach. Otherwise, during instrumented testing
    // AbstractSavedStateViewModelFactory#create in the Fragment would try to call
    // ViewModel#setTagIfAbsent which will crash because a field being referenced is null
    // due to mocking. Probably this will work if dexmaker-mockito-inline used.
    // The root issue here is that mocking a view model involves mocking a type that we do not own -
    // the base class ViewModel.
    var viewModelFactory: ViewModelProvider.Factory? = null
}