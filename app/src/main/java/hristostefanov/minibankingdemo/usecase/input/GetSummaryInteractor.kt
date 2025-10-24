package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.Outcome
import hristostefanov.minibankingdemo.usecase.output.GetSummaryUI

interface GetSummaryInteractor {
    suspend fun start(getSummaryUI: GetSummaryUI): Outcome
}