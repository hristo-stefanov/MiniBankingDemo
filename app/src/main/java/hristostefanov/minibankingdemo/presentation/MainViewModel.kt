package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.MainCommandChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stringSupplier: StringSupplier,
    // TODO do we need this here exactly? Can't RetryDialog inject it?
    val mainUiImpl: MainUiImpl,
    private val eventBus: EventBus,
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>,
    private val loginSessionRegistry: LoginSessionRegistry,
) : ViewModel()


