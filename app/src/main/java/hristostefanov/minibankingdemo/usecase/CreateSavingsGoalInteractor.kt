package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.usecase.output.UserInterface

interface CreateSavingsGoalInteractor {
    suspend fun start(userInterface: UserInterface)
    suspend fun onGoalNameSubmit(goalName: String, userInterface: UserInterface)
}