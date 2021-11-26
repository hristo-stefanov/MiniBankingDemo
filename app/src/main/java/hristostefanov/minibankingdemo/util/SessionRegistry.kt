package hristostefanov.minibankingdemo.util

interface SessionRegistry {
    var sessionComponent: SessionComponent?
    fun createSession(token: String)
    fun close()
}