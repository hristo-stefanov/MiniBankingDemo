package hristostefanov.minibankingdemo.usecase.input

interface PresentSummaryInteractor {
    suspend operator fun invoke(): Status
}