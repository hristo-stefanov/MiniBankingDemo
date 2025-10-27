package hristostefanov.minibankingdemo.usecase.input

import java.util.Currency

interface CreateSavingsGoalInteractor {
    suspend operator fun invoke(goalName: String, accountId: String, accountCurrency: Currency): Status
}