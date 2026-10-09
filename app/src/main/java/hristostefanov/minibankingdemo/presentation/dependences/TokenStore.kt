package hristostefanov.minibankingdemo.presentation.dependences

import kotlinx.coroutines.flow.StateFlow

interface TokenStore {
    val token: String?

    fun setToken(token: String?)
}