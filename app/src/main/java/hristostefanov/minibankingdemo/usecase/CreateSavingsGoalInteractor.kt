package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.usecase.output.UserInterface
import java.util.Currency

interface CreateSavingsGoalInteractor {
    suspend fun start(userInterface: UserInterface, goalName: String, accountId: String, accountCurrency: Currency): Outcome
}