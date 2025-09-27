package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.usecase.output.UserInterface

interface LogoutInteractor {
    suspend fun start(): Outcome
}