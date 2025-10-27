package hristostefanov.minibankingdemo.usecase.input

interface PresentSummaryInteractor {
    suspend fun start(): Status
}