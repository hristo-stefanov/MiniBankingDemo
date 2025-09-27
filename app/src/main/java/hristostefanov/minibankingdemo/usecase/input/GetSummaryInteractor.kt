package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.Outcome
import hristostefanov.minibankingdemo.usecase.output.UserInterface

interface GetSummaryInteractor {
    suspend fun start(userInterface: UserInterface): Outcome
}