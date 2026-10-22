package hristostefanov.minibankingdemo.usecase.input

interface DropTokenInteractor {
    suspend operator fun invoke(): Status
}