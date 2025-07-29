package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.InteractorStatus
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.flow.StateFlow

interface EnsureLoginCredentialsInteractor {
    val status: StateFlow<InteractorStatus>
    suspend fun start(userInterface: UserInterface, stopContinuation: ContinuationId)
    suspend fun resume()
    suspend fun onLoginCredentialsSubmit(loginCredentials: String, userInterface: UserInterface)
}