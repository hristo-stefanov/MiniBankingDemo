package hristostefanov.minibankingdemo.usecase.input

interface EnsureLoginCredentialsInteractor {
    suspend fun start(): Status
}