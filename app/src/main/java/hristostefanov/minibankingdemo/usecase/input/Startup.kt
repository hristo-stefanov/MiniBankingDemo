package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.InteractorStatus
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Startup {
    val status: StateFlow<InteractorStatus>
    suspend fun onAppStart(userInterface: UserInterface)
    suspend fun onLoginCredentialsSubmit(loginCredentials: String, userInterface: UserInterface)
}