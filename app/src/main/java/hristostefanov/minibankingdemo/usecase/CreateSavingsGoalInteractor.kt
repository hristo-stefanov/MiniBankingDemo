package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.usecase.output.UserInterface

interface CreateSavingsGoalInteractor : InteractorLifecycle {
    suspend fun start(userInterface: UserInterface)
    suspend fun onGoalNameSubmit(goalName: String, userInterface: UserInterface)

    suspend fun cancel(userInterface: UserInterface)
}