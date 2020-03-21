package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.ui.AccessTokenFragmentDirections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccessTokenViewModel @Inject constructor(
    private val _sharedState: SharedState
) : ViewModel() {

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _acceptCommandEnabled = MutableLiveData<Boolean>(false)
    val acceptCommandEnabled: LiveData<Boolean> = _acceptCommandEnabled

    fun onAccessTokenChanged(accessToken: String) {
        if (_sharedState.accessToken != accessToken) {
            _sharedState.accessToken = accessToken
            _acceptCommandEnabled.value = _sharedState.accessToken.isNotBlank()
        }
    }

    fun onAcceptCommand() {
        _sharedState.isMockService = false
        viewModelScope.launch {
            _navigationChannel.send(AccessTokenFragmentDirections.actionToAccountsDestination())
        }
    }

    fun onUseMockService() {
        _sharedState.isMockService = true
        viewModelScope.launch {
            _navigationChannel.send(AccessTokenFragmentDirections.actionToAccountsDestination())
        }
    }
}