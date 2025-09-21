package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.InteractorLifecycle
import hristostefanov.minibankingdemo.usecase.InteractorStatus
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.flow.Flow

interface GetSummaryInteractor : InteractorLifecycle {
    override val statusChanged: Flow<InteractorStatus>

    suspend fun start(userInterface: UserInterface)
    suspend fun onRetryLoading(userInterface: UserInterface)
}