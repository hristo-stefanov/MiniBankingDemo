package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.NavigationChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stringSupplier: StringSupplier,
    val userInterface: UserInterfaceImpl,
    private val eventBus: EventBus,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    private val loginSessionRegistry: LoginSessionRegistry,
) : ViewModel() {

    /*
    init {
        setUpTrackingInteractorStateChanges()
    }


        createSavingsGoalInteractor.statusChanged
            .onEach { status ->
                Log.d(LOG_INTERACTORS_TAG, "CreateSavingsGoalInteractor.status = $status")
                // TODO what statuses we need here?
                if (status.isFinished()) {
                    navigationChannel.send(Navigation.Backward)
                }
            }
            .launchIn(viewModelScope)
    }
     */
}
