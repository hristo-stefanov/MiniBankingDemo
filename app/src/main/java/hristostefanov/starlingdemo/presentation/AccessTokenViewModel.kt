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
    private val _sessionState: SessionState
) : ViewModel() {

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _acceptCommandEnabled = MutableLiveData<Boolean>(false)
    val acceptCommandEnabled: LiveData<Boolean> = _acceptCommandEnabled

    fun onAccessTokenChanged(accessToken: String) {
        if (_sessionState.accessToken != accessToken) {
            _sessionState.accessToken = accessToken
            _acceptCommandEnabled.value = _sessionState.accessToken.isNotBlank()
        }
    }

    fun onAcceptCommand() {
        _sessionState.isMockService = false
        viewModelScope.launch {
            _navigationChannel.send(AccessTokenFragmentDirections.actionToAccountsDestination())
        }
    }

    fun onUseMockService() {
        _sessionState.isMockService = true
        viewModelScope.launch {
            _navigationChannel.send(AccessTokenFragmentDirections.actionToAccountsDestination())
        }
    }
}