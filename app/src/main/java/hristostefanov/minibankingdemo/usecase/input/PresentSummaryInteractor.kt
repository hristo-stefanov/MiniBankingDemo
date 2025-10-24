package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.Outcome

interface PresentSummaryInteractor {
    suspend fun start(): Outcome
}