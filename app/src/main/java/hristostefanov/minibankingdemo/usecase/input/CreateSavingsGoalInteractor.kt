package hristostefanov.minibankingdemo.usecase.input

import java.util.Currency

interface CreateSavingsGoalInteractor {
    suspend fun start(goalName: String, accountId: String, accountCurrency: Currency): Status
}