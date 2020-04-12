package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import hristostefanov.starlingdemo.presentation.dependences.TokenStore
import hristostefanov.starlingdemo.ui.AccessTokenFragmentDirections
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AccessTokenViewModel(private val _state: SavedStateHandle) : ViewModel() {
    @Inject
    internal lateinit var _tokenStore: TokenStore
    @Inject
    internal lateinit var eventBus: EventBus

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
        eventBus.post(Navigation.Forward(AccessTokenFragmentDirections.actionToAccountsDestination()))
    }

    fun onUseMockService() {
        _tokenStore.token = null
        eventBus.post(Navigation.Forward(AccessTokenFragmentDirections.actionToAccountsDestination()))
    }
}