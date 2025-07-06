package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.StartupInteractor
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class LoginViewModel @Inject constructor(
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val userInterface: UserInterfaceImpl,
    private val startupInteractor: StartupInteractor,
) : ViewModel() {

    private val _acceptCommandEnabled = MutableLiveData(false)
    val acceptCommandEnabled: LiveData<Boolean> = _acceptCommandEnabled

    private var accessToken: String? = null

    fun onAccessTokenChanged(accessToken: String) {
        // SECURITY: do not save the token in SavedStateHandle, which is saved in the
        // "saved instance state" by ActivityManager service
        // this also requires EditText#saveEnabled = false !!!

        // TODO validation rule
        _acceptCommandEnabled.value = accessToken.isNotBlank()

        this.accessToken = accessToken
    }

    fun onAcceptCommand() {
        viewModelScope.launch {
            accessToken?.let {
                startupInteractor.onLoginCredentialsSubmit(it, userInterface)

                // Note this will clear this view model and cancel this coroutine so
                // should be called after calling the interactor
                navigationChannel.send(Navigation.Backward)
            }

        }
    }
}