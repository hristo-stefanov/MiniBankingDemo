package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import hristostefanov.starlingdemo.presentation.dependences.TokenStore
import hristostefanov.starlingdemo.ui.AccessTokenFragmentDirections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccessTokenViewModel  (private val _savedStateHandle: SavedStateHandle)
 : ViewModel() {
    @Inject
    internal lateinit var _tokenStore: TokenStore

    private val _navigationChannel = Channel<NavDirections>()
    val navigationChannel: ReceiveChannel<NavDirections> = _navigationChannel

    private val _acceptCommandEnabled = MutableLiveData(false)
    val acceptCommandEnabled: LiveData<Boolean> = _acceptCommandEnabled

    fun onAccessTokenChanged(accessToken: String) {
        // NOTE: do not save the token in SavedStateHandle for security reasons,
        // this also requires EditEdit#saveEnabled = false

        if (_tokenStore.token != accessToken) {
            _tokenStore.token = accessToken
            _acceptCommandEnabled.value = accessToken.isNotBlank()
        }
    }

    fun onAcceptCommand() {
        viewModelScope.launch {
            _navigationChannel.send(AccessTokenFragmentDirections.actionToAccountsDestination())
        }
    }

    fun onUseMockService() {
        _tokenStore.token = null
        viewModelScope.launch {
            _navigationChannel.send(AccessTokenFragmentDirections.actionToAccountsDestination())
        }
    }
}