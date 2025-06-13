package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.output.UserInterface

interface Startup {
    suspend fun launchApp(userInterface: UserInterface)
}