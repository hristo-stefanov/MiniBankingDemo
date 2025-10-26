package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.input.Outcome

interface LogoutInteractor {
    suspend fun start(): Outcome
}