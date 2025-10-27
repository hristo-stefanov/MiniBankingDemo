package hristostefanov.minibankingdemo.usecase.input

interface ViewSummaryInteractor {
    suspend operator fun invoke(): Status
}