package hristostefanov.minibankingdemo.usecase

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class InteractorLifecycleImpl @Inject constructor(): InteractorLifecycle {

    private val statusChannel = Channel<InteractorStatus>()

    // TODO the status needs to be saved
    private var _status = InteractorStatus.Created

    override val status
        get() = _status

    override val statusChanged: Flow<InteractorStatus> = statusChannel.receiveAsFlow()

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