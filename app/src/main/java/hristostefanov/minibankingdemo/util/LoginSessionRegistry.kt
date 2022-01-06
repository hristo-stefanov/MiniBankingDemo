package hristostefanov.minibankingdemo.util

interface LoginSessionRegistry {
    var component: LoginSessionComponent?
    fun createSession(token: String, tokenType: String)
    fun close()
}