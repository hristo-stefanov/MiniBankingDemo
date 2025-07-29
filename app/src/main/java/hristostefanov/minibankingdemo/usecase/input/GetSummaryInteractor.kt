package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.InteractorStatus
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.flow.StateFlow

interface GetSummaryInteractor {
    val status: StateFlow<InteractorStatus>
    suspend fun start(userInterface: UserInterface)
    suspend fun onRetryLoading(userInterface: UserInterface)
    suspend fun onLoginCredentialsEnsured(userInterface: UserInterface)
    suspend fun resume()
}