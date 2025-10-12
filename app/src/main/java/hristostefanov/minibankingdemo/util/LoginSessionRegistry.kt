package hristostefanov.minibankingdemo.util

interface LoginSessionRegistry {
    var component: LoginSessionComponent?
    val requireComponent: LoginSessionComponent
        get() = component ?: throw IllegalStateException("No login session")
    fun createSession(token: String, tokenType: String)
    fun close()
}