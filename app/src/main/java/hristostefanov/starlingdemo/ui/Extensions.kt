package hristostefanov.starlingdemo.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import hristostefanov.starlingdemo.App

fun Fragment.sessionComponent() = (requireContext().applicationContext as App)
    .applicationComponent
    .sessionRegistry
    .sessionComponent

fun Fragment.viewModelFactory(block: (handle: SavedStateHandle) -> ViewModel): ViewModelProvider.Factory {
    return UIUnitTestRegistry.viewModelFactory ?: ViewModelFactory(this, block)
}

private class ViewModelFactory(owner: SavedStateRegistryOwner, val block: (handle: SavedStateHandle) -> ViewModel) :
    AbstractSavedStateViewModelFactory(owner, null) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return block(handle) as T
    }
}
