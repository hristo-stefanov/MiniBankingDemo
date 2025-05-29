package hristostefanov.minibankingdemo.usecase.input

interface Startup {
    fun launchApp()
    fun submitLoginCredentials(token: String)
}