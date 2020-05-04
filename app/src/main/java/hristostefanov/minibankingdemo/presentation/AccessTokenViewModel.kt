package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AccessTokenViewModel(private val state: SavedStateHandle) : ViewModel() {
    @Inject
    internal lateinit var tokenStore: TokenStore

    @Inject @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    @Inject
    internal lateinit var eventBus: EventBus

    private val _acceptCommandEnabled = MutableLiveData(false)
    val acceptCommandEnabled: LiveData<Boolean> = _acceptCommandEnabled

    fun onAccessTokenChanged(accessToken: String) {
        // SECURITY: do not save the token in SavedStateHandle, which is saved in the
        // "saved instance state" by ActivityManager service
        // this also requires EditText#saveEnabled = false !!!
        if (tokenStore.token != accessToken) {
            tokenStore.token = accessToken
            _acceptCommandEnabled.value = accessToken.isNotBlank()
        }
    }

    fun onAcceptCommand() {
        eventBus.post(AuthenticatedEvent())
        viewModelScope.launch {
            navigationChannel.send(Navigation.Backward)
        }
    }
}