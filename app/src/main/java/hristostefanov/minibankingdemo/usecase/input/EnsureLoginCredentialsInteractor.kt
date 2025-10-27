package hristostefanov.minibankingdemo.usecase.input

interface EnsureLoginCredentialsInteractor {
    suspend operator fun invoke(): Status
}