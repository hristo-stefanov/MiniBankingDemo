package hristostefanov.minibankingdemo.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class InteractorLifecycleImpl @Inject constructor(): InteractorLifecycle {

    // TODO consider the scope of interactor instances,
    // some are @Singleton while others @LoginSessionScope
    private val scope = CoroutineScope(Dispatchers.Main)

    private val statusChannel = Channel<InteractorStatus>()

    // TODO the status needs to be saved
    private var _status = InteractorStatus.Created

    override val status
        get() = _status

    // Note, we need to share the flow across all subscribers and handle
    // the situation of no subscribers
    override val statusChanged: Flow<InteractorStatus> = statusChannel.receiveAsFlow().shareIn(scope, SharingStarted.Eagerly)

    override suspend fun resume() {
        // TODO restore status?
        //
        // for now I assume this function is called for Started
        // interactors only

        setStatus(InteractorStatus.Started)
    }

    internal suspend fun setStatus(status: InteractorStatus) {
        _status = status
        statusChannel.send(status)
    }
}