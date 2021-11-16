package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class AccessTokenViewModel @Inject constructor(
    private val tokenStore: TokenStore,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val eventBus: EventBus
) : ViewModel() {

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