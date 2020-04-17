package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.*
import hristostefanov.starlingdemo.presentation.dependences.TokenStore
import hristostefanov.starlingdemo.ui.AccessTokenFragmentDirections
import hristostefanov.starlingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccessTokenViewModel(private val _state: SavedStateHandle) : ViewModel() {
    @Inject
    internal lateinit var _tokenStore: TokenStore
    @Inject @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

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
            navigationChannel.send(Navigation.Forward(AccessTokenFragmentDirections.actionToAccountsDestination()))
        }
    }

    fun onUseMockService() {
        _tokenStore.token = null
        viewModelScope.launch {
            navigationChannel.send(Navigation.Forward(AccessTokenFragmentDirections.actionToAccountsDestination()))
        }
    }
}