package hristostefanov.minibankingdemo.usecase.input

interface LogoutInteractor {
    suspend fun start(): Status
}