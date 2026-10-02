package hristostefanov.minibankingdemo.util

interface LoginSessionRegistry {
    val component: LoginSessionComponent?
    // TODO this can crash
    val requireComponent: LoginSessionComponent
        get() = component ?: throw IllegalStateException("No login session")
    fun createSession(token: String, tokenType: String)
    fun closeSession()
}