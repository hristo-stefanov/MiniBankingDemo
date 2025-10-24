package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.usecase.output.StockUI
import java.util.Currency

interface CreateSavingsGoalInteractor {
    suspend fun start(stockUI: StockUI, goalName: String, accountId: String, accountCurrency: Currency): Outcome
}