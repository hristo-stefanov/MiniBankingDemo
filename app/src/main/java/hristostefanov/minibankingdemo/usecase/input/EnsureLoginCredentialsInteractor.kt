package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.InteractorLifecycle
import hristostefanov.minibankingdemo.usecase.InteractorStatus
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.flow.Flow

interface EnsureLoginCredentialsInteractor : InteractorLifecycle {
    override val statusChanged: Flow<InteractorStatus>
    override suspend fun resume()

    suspend fun start(userInterface: UserInterface, stopContinuation: ContinuationId)
    suspend fun onLoginCredentialsSubmit(loginCredentials: String, userInterface: UserInterface)
}