package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.Outcome

interface EnsureLoginCredentialsInteractor {
    suspend fun start(): Outcome
}