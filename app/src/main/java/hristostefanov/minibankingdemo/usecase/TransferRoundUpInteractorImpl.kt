package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject

class TransferRoundUpInteractorImpl @Inject constructor(
    private val sessionRegistry: LoginSessionRegistry,
) : TransferRoundUpInteractor {

    suspend fun start(userInterface: UserInterface) {
    }

    override fun onAccountSelected(param: String, userInterface: UserInterface) {
        TODO("Not yet implemented")
    }
}