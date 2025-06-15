package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.usecase.input.Startup
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.Continuation

@HiltViewModel
class MainViewModel @Inject constructor(
    startup: Startup,
    userInterface: UserInterfaceImpl
): ViewModel() {
    init {
        viewModelScope.launch {
            startup.launchApp(userInterface)
        }
    }
}