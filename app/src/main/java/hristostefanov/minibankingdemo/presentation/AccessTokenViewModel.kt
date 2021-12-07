package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.BuildConfig
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.SessionRegistry
import hristostefanov.minibankingdemo.util.oauth.OAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class AccessTokenViewModel @Inject constructor(
    private val tokenStore: TokenStore,
    private val sessionRegistry: SessionRegistry,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val eventBus: EventBus,
    private val oAuth: OAuth
) : ViewModel() {

    private val _acceptCommandEnabled = MutableLiveData(false)
    val acceptCommandEnabled: LiveData<Boolean> = _acceptCommandEnabled

    init {
        if (tokenStore.refreshToken.isBlank()) {
            // ask for credentials
        } else {
            // auto-login by refreshing the token
            refreshTokenAndGoBack()
        }
    }

    fun onAccessTokenChanged(refreshToken: String) {
        // SECURITY: do not save the token in SavedStateHandle, which is saved in the
        // "saved instance state" by ActivityManager service
        // this also requires EditText#saveEnabled = false !!!
        if (tokenStore.refreshToken != refreshToken) {
            tokenStore.refreshToken = refreshToken
            _acceptCommandEnabled.value = refreshToken.isNotBlank()
        }
    }

    fun onAcceptCommand() {
        refreshTokenAndGoBack()
    }

    private fun refreshTokenAndGoBack() {
        viewModelScope.launch {
            val response = oAuth.accessToken(
                client_id = BuildConfig.CLIENT_ID,
                client_secret = BuildConfig.CLIENT_SECRET,
                grant_type = "refresh_token",
                tokenStore.refreshToken
            )

            tokenStore.refreshToken = response.refresh_token
            // TODO pass token_type as session argument and make retrofit use it instead
            // of hardcoded "bearer"
            sessionRegistry.createSession(response.access_token, response.token_type)

            eventBus.post(AuthenticatedEvent())
            navigationChannel.send(Navigation.Backward)
        }
    }
}