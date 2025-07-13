package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.InteractorStatus
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.util.Currency

interface PresentAccountsAndRoundupsSummary {
    val status: StateFlow<InteractorStatus>
    suspend fun start(userInterface: UserInterface)
    suspend fun onRetryLoading(userInterface: UserInterface)
}