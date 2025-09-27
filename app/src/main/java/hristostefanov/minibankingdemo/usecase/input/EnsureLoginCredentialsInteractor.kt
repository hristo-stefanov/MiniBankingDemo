package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.Outcome
import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI

interface EnsureLoginCredentialsInteractor {
    suspend fun start(userInterface: EnsureLoginCredentialsUI): Outcome
}