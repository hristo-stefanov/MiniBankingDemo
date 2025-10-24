package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.Outcome
import hristostefanov.minibankingdemo.usecase.output.StockUI
import java.math.BigDecimal
import java.util.Currency

interface TransferRoundUpInteractor {
    suspend fun start(
        accountId: String,
        accountCurrency: Currency,
        roundUpAmount: BigDecimal,
        savingsGoalId: String
    ): Outcome
}