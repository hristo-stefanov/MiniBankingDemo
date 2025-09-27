package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.usecase.output.UserInterface

interface LogoutInteractor: InteractorLifecycle {
    suspend fun start(userInterface: UserInterface)
}