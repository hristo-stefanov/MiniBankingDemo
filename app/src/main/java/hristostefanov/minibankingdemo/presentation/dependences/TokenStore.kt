package hristostefanov.minibankingdemo.presentation.dependences

import kotlinx.coroutines.flow.StateFlow

interface TokenStore {
    val tokenFlow: StateFlow<String?>

    fun setToken(token: String?)
}