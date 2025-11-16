package hristostefanov.minibankingdemo.util

import kotlinx.coroutines.flow.StateFlow

interface LoginSessionRegistry {
    val component: LoginSessionComponent?
    val componentFlow: StateFlow<LoginSessionComponent?>
    val requireComponent: LoginSessionComponent
        get() = component ?: throw IllegalStateException("No login session")
    fun createSession(token: String, tokenType: String)
    fun closeSession()
}