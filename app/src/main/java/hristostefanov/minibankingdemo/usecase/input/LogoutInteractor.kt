package hristostefanov.minibankingdemo.usecase.input

interface LogoutInteractor {
    suspend operator fun invoke(): Status
}