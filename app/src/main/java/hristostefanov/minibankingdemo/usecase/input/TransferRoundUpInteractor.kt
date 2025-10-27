package hristostefanov.minibankingdemo.usecase.input

import java.math.BigDecimal
import java.util.Currency

interface TransferRoundUpInteractor {
    suspend operator fun invoke(
        accountId: String,
        accountCurrency: Currency,
        roundUpAmount: BigDecimal,
        savingsGoalId: String
    ): Status
}