package hristostefanov.minibankingdemo.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.usecase.input.Startup
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.Continuation

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    startup: Startup,
    userInterface: UserInterfaceImpl
): ViewModel() {
    // TODO remove this view model?
}