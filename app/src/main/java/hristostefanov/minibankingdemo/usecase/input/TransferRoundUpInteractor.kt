package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.output.UserInterface

interface TransferRoundUpInteractor {
    fun onAccountSelected(param: String, userInterface: UserInterface)
}