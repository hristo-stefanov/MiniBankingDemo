package hristostefanov.minibankingdemo.util

interface SessionRegistry {
    var sessionComponent: SessionComponent?
    fun createSession(token: String, tokenType: String)
    fun close()
}