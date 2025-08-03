package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.InteractorLifecycle
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import java.math.BigDecimal
import java.util.Currency

interface TransferRoundUpInteractor : InteractorLifecycle {
    suspend fun start(
        accountId: String,
        accountCurrency: Currency,
        roundUpAmount: BigDecimal,
        userInterface: UserInterface
    )
    suspend fun onSavingsGaolSelected(id: String, userInterface: UserInterface)
    suspend fun onTransferConfirmed(userInterface: UserInterface)
}