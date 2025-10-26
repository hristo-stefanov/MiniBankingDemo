package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.input.Outcome

interface PresentSummaryInteractor {
    suspend fun start(): Outcome
}