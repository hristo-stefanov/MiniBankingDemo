package hristostefanov.minibankingdemo.usecase

interface LogoutInteractor {
    suspend fun start(): Outcome
}